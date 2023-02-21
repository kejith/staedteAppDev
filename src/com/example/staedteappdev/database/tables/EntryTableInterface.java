package com.example.staedteappdev.database.tables;

import android.provider.BaseColumns;

public interface EntryTableInterface extends BaseColumns {
    /*
     * The Table Name
     */
	public static final String TABLE_NAME = "entry";
    
    /* 
     * Columns for the table
     */
	public static final String COLUMN_ID = BaseColumns._ID;
    public static final String COLUMN_NAME_ENTRY_ID = "entry_id";
    public static final String COLUMN_NAME_TITLE = "entry_title";
    public static final String COLUMN_NAME_DESCRIPTION = "entry_description";
    public static final String COLUMN_NAME_LATITUDE = "enty_latitude";
    public static final String COLUMN_NAME_LONGITUDE = "entry_longitude";
    public static final String COLUMN_NAME_CATEGORY_ID = "entry_category_id";
    
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
              COLUMN_ID + ","+
              COLUMN_NAME_ENTRY_ID +" int(11) NOT NULL UNIQUE,"+
			  COLUMN_NAME_TITLE +" text NOT NULL,"+
			  COLUMN_NAME_DESCRIPTION +" text NOT NULL,"+
			  COLUMN_NAME_LATITUDE +" double NOT NULL,"+
			  COLUMN_NAME_LONGITUDE +" double NOT NULL,"+
			  COLUMN_NAME_CATEGORY_ID +" int(11) NOT NULL,"+
			" PRIMARY KEY ("+ COLUMN_NAME_ENTRY_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}