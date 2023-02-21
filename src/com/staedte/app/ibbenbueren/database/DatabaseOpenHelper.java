package com.staedte.app.ibbenbueren.database;

import java.util.Formatter;

import com.staedte.app.ibbenbueren.database.tables.AddressTableInterface;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "meinNordhorn.db";

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO onUpgrade for Database Tables in Entry Open Helper		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.createTables(db);
	}
	
	private void createTables(SQLiteDatabase db){
		db.execSQL(SourceTableInterface.TABLE_CREATE);
		db.execSQL(AddressTableInterface.TABLE_CREATE);
		db.execSQL(CategoryTableInterface.TABLE_CREATE);
		db.execSQL(EntryTableInterface.TABLE_CREATE);
		db.execSQL(LabelTableInterface.TABLE_CREATE);
	}
	
	public void recreateTables(){
		SQLiteDatabase db = this.getWritableDatabase();
		
		this.deleteAllTables(db);
		this.createTables(db);
		
		db.close();
	}
	
	public void deleteAllTables(SQLiteDatabase db){
		String sql = "";
		String[] tableNames = new String[] {
				EntryTableInterface.TABLE_NAME,
				CategoryTableInterface.TABLE_NAME,
				AddressTableInterface.TABLE_NAME,
				SourceTableInterface.TABLE_NAME,
				LabelTableInterface.TABLE_NAME
		};
		
		for(String tableName : tableNames)
			sql = sql.concat(String.format("DROP TABLE IF EXISTS %s;",tableName));
		
		db.execSQL(sql);
	}
	
	public void deleteTable(String tableName, SQLiteDatabase db){
		String sql = String.format("DROP TABLE IF EXISTS %s;", tableName);
		db.execSQL(sql);
	}
	
	public void recreateTable(String tableName, String tableCreateStatement){
		SQLiteDatabase db = this.getWritableDatabase();
		
		// delete table with tableName
		this.deleteTable(tableName, db);
		
		// create table from statement
		db.execSQL(tableCreateStatement);
		
		db.close();
	}
	
	@SuppressWarnings("unused")
	private void log(String message){
		Log.d(this.getClass().getName(), message);
	}
}
