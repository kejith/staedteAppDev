package com.staedte.app.ibbenbueren.contentProvider;

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

import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;
import com.staedte.app.ibbenbueren.lib.Source;

public class SourceProvider extends ContentProvider {

	// Saves the Context so we have access from inner-class methods
	private Context context;
	
	// Helper to get access to the database
	private DatabaseOpenHelper dbHelper;
	
	// parts of the URI. saved in parts so we can get them later
	private static final String AUTHORITY = "com.example.staedteappdev.contentProvider.SourceProvider";
	private static final String BASE_PATH = DatabaseOpenHelper.DATABASE_NAME;
	
	// provides the URI for use outside the class
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);
	
	// to find the right URI
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);;
	
	// CODEs for the URI-Matcher
	public static final int SOURCE = 1;
	public static final int SOURCE_ID = 2;
	
	// chooses the right URI based on the CODE
	static {
		uriMatcher.addURI(
				AUTHORITY, 
				SourceTableInterface.TABLE_NAME,
				SOURCE);
		
		uriMatcher.addURI(
				AUTHORITY, 
				BASE_PATH + "/#", 
				SOURCE_ID);
	}
	
	public SourceProvider(){
	}
	
	public SourceProvider(Context context){
		this.context = context;
		this.dbHelper = new DatabaseOpenHelper(this.context);
	}
	
	@Override
	public boolean onCreate() {
		this.context = getContext();
		this.dbHelper = new DatabaseOpenHelper(this.context);
		
		return (this.context != null && this.dbHelper != null);
	}
	
	@Override
	public String getType(Uri uri) {		
		switch(uriMatcher.match(uri)){
		case SOURCE:
			// URI: every row
			return "vnd.android.cursor.dir/vnd." + AUTHORITY +""+ SourceTableInterface.TABLE_NAME;
		case SOURCE_ID:
			// URI: a single row
			return "vnd.android.cursor.item/vnd." + AUTHORITY +""+ SourceTableInterface.TABLE_NAME;
		default:
			// unsupported URI
			throw new IllegalArgumentException("Unsupported URI: "+ uri);
		}
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long rowID = db.insert(SourceTableInterface.TABLE_NAME, "", values);
		
		if(rowID > 0){
			Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
			return result;
		}
		
		throw new SQLException("Failed to insert row into "+ uri);
	}
	public Uri replace(Uri uri, ContentValues values) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		long rowID = db.replace(SourceTableInterface.TABLE_NAME, "", values);
		
		if(rowID > 0){
			Uri result = ContentUris.withAppendedId(CONTENT_URI, rowID);
			return result;
		}
		
		throw new SQLException("Failed to insert row into "+ uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		sqlBuilder.setTables(SourceTableInterface.TABLE_NAME);
		
		// define sort order if is not set
		if(sortOrder == null || sortOrder == "")
			sortOrder = EntryTableInterface.COLUMN_ID;
		
		// Create Cursor pointing on the first element in the list
		Cursor c = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		Log.d("SourceProvider.java", sqlBuilder.buildQueryString(false, "source", projection, selection, null, null, null, null));
			
		
		c.setNotificationUri(context.getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		int count = 0;
		
		switch(SourceProvider.uriMatcher.match(uri)){
		case SOURCE:
			// update multiple rows
			count = db.update(SourceTableInterface.TABLE_NAME, values, selection, selectionArgs);
			break;
		case SOURCE_ID:
			// update a single row
			count = db.update(
				SourceTableInterface.TABLE_NAME, 
				values, 
				SourceTableInterface.COLUMN_ID 
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
		
		switch(SourceProvider.uriMatcher.match(uri)){
		case SOURCE:
			// delete multiple rows
			count = db.delete(EntryTableInterface.TABLE_NAME, selection, selectionArgs);
			break;
		case SOURCE_ID:
			// delete a single row
			count = db.delete(
				SourceTableInterface.TABLE_NAME,
				SourceTableInterface.COLUMN_ID
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
	
	public Cursor getCursorOfSourcesByID(int id){
		String selection = " "+SourceTableInterface.COLUMN_SOURCES_PARENT+"=? AND "+ SourceTableInterface.COLUMN_SOURCES_PARENT_TYPE+"=? ";
		
		return this.query(
				SourceProvider.CONTENT_URI, 
				new String[]{SourceTableInterface.COLUMN_ID, SourceTableInterface.COLUMN_SOURCES_LINK},
				selection, 
				new String[]{id +"", Source.ENTRY_IMAGE+""}, 
				null
		);
	}

}
