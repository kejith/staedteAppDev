package com.staedte.app.ibbenbueren.contentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.staedte.app.ibbenbueren.database.LabelOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;

public class LabelListProvider extends ContentProvider {
		// Saves the Context so we have access from inner-class methods
		private Context context;
		
		// Helper to get access to the database
		private SQLiteOpenHelper dbHelper;
		
		// parts of the URI. saved in parts so we can get them later
		private static final String AUTHORITY = "com.example.staedteappdev.contentProvider.CategoryListProvider";
		private static final String BASE_PATH = LabelOpenHelper.DATABASE_NAME;
		
		// provides the URI for use outside the class
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		
		// to find the right URI
		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
		
		// CODEs for the URI-Matcher
		public static final int LABEL = 1;
		public static final int LABEL_ID = 2;
		
		// chooses the right URI based on the CODE
		static {
			uriMatcher.addURI(
					AUTHORITY, 
					LabelTableInterface.TABLE_NAME,
					LABEL);
			
			uriMatcher.addURI(
					AUTHORITY, 
					BASE_PATH + "/#", 
					LABEL_ID);
		}
		
		public LabelListProvider(){
		}
		
		public LabelListProvider(Context context){
			this.context = context;
			this.dbHelper = new LabelOpenHelper(this.context);
		}
		
		@Override
		public boolean onCreate() {
			this.context = getContext();
			this.dbHelper = new LabelOpenHelper(this.context);
			
			if(this.context != null && this.dbHelper != null){
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String getType(Uri uri) {		
			switch(uriMatcher.match(uri)){
			case LABEL:
				// URI: every row
				return "vnd.android.cursor.dir/vnd." + AUTHORITY +""+ LabelTableInterface.TABLE_NAME;
			case LABEL_ID:
				// URI: a single row
				return "vnd.android.cursor.item/vnd." + AUTHORITY +""+ LabelTableInterface.TABLE_NAME;
			default:
				// unsupported URI
				throw new IllegalArgumentException("Unsupported URI: "+ uri);
			}
		}
		
		@Override
		public Uri insert(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.insert(LabelTableInterface.TABLE_NAME, "", values);
			
			if(rowID > 0){
				Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
				this.context.getContentResolver().notifyChange(result, null);
				return result;
			}
			
			throw new SQLException("Failed to insert row into "+ uri);
		}
		public Uri replace(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.replace(LabelTableInterface.TABLE_NAME, "", values);
			
			if(rowID > 0){
				Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
				this.context.getContentResolver().notifyChange(result, null);
				return result;
			}
			
			throw new SQLException("Failed to insert row into "+ uri);
		}

		@Override
		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
				String sortOrder) {
			
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
			sqlBuilder.setTables(LabelTableInterface.TABLE_NAME);
			
			// one specific row?
			if(uriMatcher.match(uri) == LabelListProvider.LABEL_ID)
				sqlBuilder.appendWhere(LabelTableInterface.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			
			// define sort order if is not set
			if(sortOrder == null || sortOrder == "")
				sortOrder = LabelTableInterface.COLUMN_ID;
			
			// Create Cursor pointing on the first element in the list
			Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);			
			c.setNotificationUri(context.getContentResolver(), uri);
			
			return c;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			int count = 0;
			
			switch(LabelListProvider.uriMatcher.match(uri)){
			case LABEL:
				// update multiple rows
				count = db.update(LabelTableInterface.TABLE_NAME, values, selection, selectionArgs);
				break;
			case LABEL_ID:
				// update a single row
				count = db.update(
					LabelTableInterface.TABLE_NAME, 
					values, 
					LabelTableInterface.COLUMN_ID
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
			
			switch(LabelListProvider.uriMatcher.match(uri)){
			case LABEL:
				// delete multiple rows
				count = db.delete(LabelTableInterface.TABLE_NAME, selection, selectionArgs);
				break;
			case LABEL_ID:
				// delete a single row
				count = db.delete(
					LabelTableInterface.TABLE_NAME,
					LabelTableInterface.COLUMN_ID 
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
			db.execSQL("TRUNCATE TABLE "+ LabelTableInterface.TABLE_NAME +";");
		}

	}