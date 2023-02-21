package com.staedte.app.ibbenbueren.contentProvider;

import java.util.Arrays;

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
import android.util.Log;

import com.staedte.app.ibbenbueren.database.CategoryOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;

public class CategoryListProvider extends ContentProvider {
		// Saves the Context so we have access from inner-class methods
		private Context context;
		
		// Helper to get access to the database
		private SQLiteOpenHelper dbHelper;
		
		// parts of the URI. saved in parts so we can get them later
		private static final String AUTHORITY = "com.example.staedteappdev.contentProvider.CategoryListProvider";
		private static final String BASE_PATH = CategoryOpenHelper.DATABASE_NAME;
		
		// provides the URI for use outside the class
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
		
		// to find the right URI
		private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
		
		// CODEs for the URI-Matcher
		public static final int CATEGORY = 1;
		public static final int CATEGORY_ID = 2;
		
		// chooses the right URI based on the CODE
		static {
			uriMatcher.addURI(
					AUTHORITY, 
					CategoryTableInterface.TABLE_NAME,
					CATEGORY);
			
			uriMatcher.addURI(
					AUTHORITY, 
					BASE_PATH + "/#", 
					CATEGORY_ID);
		}
		
		public CategoryListProvider(){
		}
		
		public CategoryListProvider(Context context){
			this.context = context;
			this.dbHelper = new CategoryOpenHelper(this.context);
		}
		
		@Override
		public boolean onCreate() {
			this.context = getContext();
			this.dbHelper = new CategoryOpenHelper(this.context);
			
			if(this.context != null && this.dbHelper != null){
				return true;
			} else {
				return false;
			}
		}
		
		@Override
		public String getType(Uri uri) {		
			switch(uriMatcher.match(uri)){
			case CATEGORY:
				// URI: every row
				return "vnd.android.cursor.dir/vnd." + AUTHORITY +""+ CategoryTableInterface.TABLE_NAME;
			case CATEGORY_ID:
				// URI: a single row
				return "vnd.android.cursor.item/vnd." + AUTHORITY +""+ CategoryTableInterface.TABLE_NAME;
			default:
				// unsupported URI
				throw new IllegalArgumentException("Unsupported URI: "+ uri);
			}
		}
		
		@Override
		public Uri insert(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.insert(CategoryTableInterface.TABLE_NAME, "", values);
			
			if(rowID > 0){
				Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
				this.context.getContentResolver().notifyChange(result, null);
				return result;
			}
			
			throw new SQLException("Failed to insert row into "+ uri);
		}
		public Uri replace(Uri uri, ContentValues values) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			long rowID = db.replace(CategoryTableInterface.TABLE_NAME, "", values);
			
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
			sqlBuilder.setTables(CategoryTableInterface.TABLE_NAME);
			
			// one specific row?
			if(uriMatcher.match(uri) == CategoryListProvider.CATEGORY_ID)
				sqlBuilder.appendWhere(CategoryTableInterface.COLUMN_ID + "=" + uri.getPathSegments().get(1));
			
			// define sort order if is not set
			if(sortOrder == null || sortOrder == "")
				sortOrder = LabelTableInterface.COLUMN_ID;
			
			if(selectionArgs != null && selectionArgs.length > 0){
				sqlBuilder.appendWhere(CategoryTableInterface.COLUMN_PARENT +"= ?");
			}
			
			// Create Cursor pointing on the first element in the list
			Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
			Log.d("EntryListProvider", Arrays.toString(c.getColumnNames()));
			
			c.setNotificationUri(context.getContentResolver(), uri);
			
			//db.close();
			return c;
		}

		@Override
		public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
			SQLiteDatabase db = this.dbHelper.getWritableDatabase();
			int count = 0;
			
			switch(CategoryListProvider.uriMatcher.match(uri)){
			case CATEGORY:
				// update multiple rows
				count = db.update(CategoryTableInterface.TABLE_NAME, values, selection, selectionArgs);
				break;
			case CATEGORY_ID:
				// update a single row
				count = db.update(
					CategoryTableInterface.TABLE_NAME, 
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
			
			switch(CategoryListProvider.uriMatcher.match(uri)){
			case CATEGORY:
				// delete multiple rows
				count = db.delete(CategoryTableInterface.TABLE_NAME, selection, selectionArgs);
				break;
			case CATEGORY_ID:
				// delete a single row
				count = db.delete(
					CategoryTableInterface.TABLE_NAME,
					CategoryTableInterface.COLUMN_ID 
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
			db.execSQL("TRUNCATE TABLE "+ CategoryTableInterface.TABLE_NAME +";");
		}

	}