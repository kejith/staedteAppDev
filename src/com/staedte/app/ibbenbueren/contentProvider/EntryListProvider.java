package com.staedte.app.ibbenbueren.contentProvider;

import java.util.Arrays;	

import org.apache.commons.lang3.StringUtils;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.staedte.app.ibbenbueren.database.EntryOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;

public class EntryListProvider extends ContentProvider {
		// Saves the Context so we have access from inner-class methods
		private Context context;
		
		// Helper to get access to the database
		private EntryOpenHelper dbHelper;
		
		// parts of the URI. saved in parts so we can get them later
		private static final String AUTHORITY = "com.staedte.app.ibbenbueren.contentProvider.EntryListProvider";
		private static final String BASE_PATH = EntryOpenHelper.DATABASE_NAME;
		
		// provides the URI for use outside the class
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		public static final Uri LABELS_JOIN = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH + "/join_labels");
		
		// to find the right URI
		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
		
		// CODEs for the URI-Matcher
		public static final int ENTRY = 1;
		public static final int ENTRY_ID = 2;
		public static final int JOIN_WITH_LABELS = 3;
		
		// chooses the right URI based on the CODE
		static {
			uriMatcher.addURI(
					AUTHORITY, 
					EntryTableInterface.TABLE_NAME,
					ENTRY);
			
			uriMatcher.addURI(
					AUTHORITY, 
					BASE_PATH + "/#", 
					ENTRY_ID);
			
			uriMatcher.addURI(
					AUTHORITY, 
					BASE_PATH + "/join_labels",
					JOIN_WITH_LABELS);
			
		}
		
		public EntryListProvider(){
		}
		
		public EntryListProvider(Context context){
			this.context = context;
			this.dbHelper = new EntryOpenHelper(this.context);
		}
		
		@Override
		public boolean onCreate() {
			Log.d("EntryListProvider", "EntryListProvider.onCreate() is beeing used");
			this.context = getContext();
			this.dbHelper = new EntryOpenHelper(this.context);
			
			Log.d("EntryListProvider", this.toString());
			
			if(this.context != null && this.dbHelper != null){
				Log.d("EntryListProvider", "EntryListProvider.onCreate() is returning true");
				return true;
			} else {
				Log.d("EntryListProvider", "EntryListProvider.onCreate() is returning false");
				return false;
			}
		}
		
		@Override
		public String getType(Uri uri) {		
			switch(uriMatcher.match(uri)){
			case ENTRY:
				// URI: every row
				return "vnd.android.cursor.dir/vnd." + AUTHORITY +""+ EntryTableInterface.TABLE_NAME;
			case ENTRY_ID:
				// URI: a single row
				return "vnd.android.cursor.item/vnd." + AUTHORITY +""+ EntryTableInterface.TABLE_NAME;
			default:
				// unsupported URI
				throw new IllegalArgumentException("Unsupported URI: "+ uri);
			}
		}
		
		@Override
		public Uri insert(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.insert(EntryTableInterface.TABLE_NAME, "", values);
			
			if(rowID > 0){
				Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
				this.context.getContentResolver().notifyChange(result, null);
				return result;
			}
			
			throw new SQLException("Failed to insert row into "+ uri);
		}
		public Uri replace(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.replace(EntryTableInterface.TABLE_NAME, "", values);
			
			if(rowID > 0){
				Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
				this.context.getContentResolver().notifyChange(result, null);
				return result;
			}
			
			throw new SQLException("Failed to insert row into "+ uri);
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,	String sortOrder) {
			
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			Cursor c = null;
			if(uriMatcher.match(uri) != EntryListProvider.JOIN_WITH_LABELS){
				c = this.queryJoinWithLabels(db, projection, selection, uri, selectionArgs, sortOrder);
			} else {
				String columns = "*, count(e._id) as eCount, e."+ EntryTableInterface.COLUMN_ID;//StringUtils.join(projection, ",");
				String table = LabelTableInterface.TABLE_NAME;
				String join = String.format("JOIN %s e ON %s.%s = e.%s LEFT OUTER JOIN %s s ON %s.%s = s.%s",
						EntryTableInterface.TABLE_NAME,
						table,
						LabelTableInterface.COLUMN_ENTRY_ID,
						EntryTableInterface.COLUMN_ID,
						SourceTableInterface.TABLE_NAME, 
						table,
						LabelTableInterface.COLUMN_ENTRY_ID,
						SourceTableInterface.COLUMN_SOURCES_PARENT
				);
				
				String where = "";
				if(selectionArgs != null)
					where = " WHERE "+ LabelTableInterface.COLUMN_CATEGORY_ID +" = "+ selectionArgs[0];
				
				String groupBy = String.format(" GROUP BY %s.%s", table,EntryTableInterface.COLUMN_ID);
				String having = "";
				String orderBy = String.format(" ORDER BY eCount DESC", table,EntryTableInterface.COLUMN_ID);
					//orderBy = sortOrder;
				
				String sql = String.format("SELECT %s FROM %s %s %s %s %s %s", 
						columns, table, join, where, groupBy, having, orderBy);	
				
				Log.d("EntryListProvider", sql);
				
				c = db.rawQuery(sql, null);
				
				return c;
				
//				sql = "SELECT * FROM "+ LabelTableInterface.TABLE_NAME +" l "
//						+ "JOIN "+ EntryTableInterface.TABLE_NAME +" e "
//						+ "ON l."+ LabelTableInterface.COLUMN_ENTRY_ID +" = e."+ EntryTableInterface.COLUMN_ID;
//				
//				if(selectionArgs != null)
//					sql +=  " WHERE "+ LabelTableInterface.COLUMN_CATEGORY_ID +" = ?";
//				
//				c = db.rawQuery(sql, selectionArgs);
			}
			
			return c;
		}
		
		public Cursor queryJoinWithLabels(SQLiteDatabase db, String[] projection, String selection, Uri uri, String[] selectionArgs, String sortOrder){
			SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
			
			sqlBuilder.setTables(EntryTableInterface.TABLE_NAME);
			
			Cursor c = null;
			// one specific row?
			if(uriMatcher.match(uri) == EntryListProvider.ENTRY_ID)
				sqlBuilder.appendWhere(EntryTableInterface.COLUMN_NAME_ENTRY_ID + "=" + uri.getPathSegments().get(1));
			
			// define sort order if is not set
			if(sortOrder == null || sortOrder == "")
				sortOrder = EntryTableInterface.COLUMN_NAME_ENTRY_ID;
			
			// Create Cursor pointing on the first element in the list
			c = sqlBuilder.query(db, projection, selection, selectionArgs, "  GROUP BY "+ EntryTableInterface.COLUMN_ID, null, sortOrder);
			
			c.setNotificationUri(context.getContentResolver(), uri);
			return c;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			int count = 0;
			
			switch(EntryListProvider.uriMatcher.match(uri)){
			case ENTRY:
				// update multiple rows
				count = db.update(EntryTableInterface.TABLE_NAME, values, selection, selectionArgs);
				break;
			case ENTRY_ID:
				// update a single row
				count = db.update(
					EntryTableInterface.TABLE_NAME, 
					values, 
					EntryTableInterface.COLUMN_NAME_ENTRY_ID 
					+ "="
					+ uri.getPathSegments().get(1) 
					+ (!TextUtils.isEmpty(selection) ? " AND ("+ selection +")" : ""), // if selction is not empty append
					selectionArgs);
					break;
			default:
				throw new IllegalArgumentException("Unknown Uri "+ uri);
			}
			
			context.getContentResolver().notifyChange(uri, null);
			return count;
		}

		@Override
		public int delete(Uri uri, String selection, String[] selectionArgs) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			int count = 0;
			
			switch(EntryListProvider.uriMatcher.match(uri)){
			case ENTRY:
				// delete multiple rows
				count = db.delete(EntryTableInterface.TABLE_NAME, selection, selectionArgs);
				break;
			case ENTRY_ID:
				// delete a single row
				count = db.delete(
					EntryTableInterface.TABLE_NAME,
					EntryTableInterface.COLUMN_NAME_ENTRY_ID 
					+ "="
					+ uri.getPathSegments().get(1) 
					+ (!TextUtils.isEmpty(selection) ? " AND ("+ selection +")" : ""), // if selction is not empty append
					selectionArgs);
					break;
			default:
				throw new IllegalArgumentException("Unknown Uri "+ uri);
			
			}
			
			context.getContentResolver().notifyChange(uri, null);
			return count;
		}
		
		public void truncateTable(){
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.execSQL("TRUNCATE TABLE "+ EntryTableInterface.TABLE_NAME +";");
		}

	}