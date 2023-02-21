package com.example.staedteappdev.contentProvider;

import java.util.Arrays;

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

import com.example.staedteappdev.database.EntryOpenHelper;
import com.example.staedteappdev.database.tables.EntryTableInterface;

public class EntryListProvider extends ContentProvider {
		// Saves the Context so we have access from inner-class methods
		private Context context;
		
		// Helper to get access to the database
		private EntryOpenHelper dbHelper;
		
		// parts of the URI. saved in parts so we can get them later
		private static final String AUTHORITY = "com.example.staedteappdev.contentProvider.EntryListProvider";
		private static final String BASE_PATH = EntryOpenHelper.DATABASE_NAME;
		
		// provides the URI for use outside the class
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		
		// to find the right URI
		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
		
		// CODEs for the URI-Matcher
		public static final int ENTRY = 1;
		public static final int ENTRY_ID = 2;
		
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
		public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
				String sortOrder) {
			
			Log.d("EntryListProvider", this.toString());

			Log.d("EntryListProvider", "EntryListProvider.query(): dbHelper is " + (dbHelper == null ? "null" : "not null"));
			Log.d("EntryListProvider", "EntryListProvider.query(): context is " + (context == null ? "null" : "not null"));
			
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
			sqlBuilder.setTables(EntryTableInterface.TABLE_NAME);
			
			Log.d("EntryListProvider", uri.toString());
			
			// one specific row?
			if(uriMatcher.match(uri) == EntryListProvider.ENTRY_ID)
				sqlBuilder.appendWhere(EntryTableInterface.COLUMN_NAME_ENTRY_ID + "=" + uri.getPathSegments().get(1));
			
			// define sort order if is not set
			if(sortOrder == null || sortOrder == "")
				sortOrder = EntryTableInterface.COLUMN_NAME_ENTRY_ID;
			
			// Create Cursor pointing on the first element in the list
			Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			Log.d("EntryListProvider", Arrays.toString(c.getColumnNames()));
			
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