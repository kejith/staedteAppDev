package com.staedte.app.ibbenbueren.database;

import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EntryOpenHelper extends SQLiteOpenHelper implements EntryTableInterface {

    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "meinNordhorn.db";

    public EntryOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO onUpgrade for Database Tables in Entry Open Helper
	}
	
}
