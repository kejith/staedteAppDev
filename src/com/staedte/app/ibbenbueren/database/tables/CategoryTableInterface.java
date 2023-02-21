package com.staedte.app.ibbenbueren.database.tables;

import android.provider.BaseColumns;

public interface CategoryTableInterface extends BaseColumns {
    /*
     * The Table Name
     */
	public static final String TABLE_NAME = "category";
    
    /* 
     * Columns for the table
     */
	public static final String COLUMN_ID 					= BaseColumns._ID;
	public static final String COLUMN_CATEGORY_ID			= "category_id";
    public static final String COLUMN_NAME		 			= "category_name";
    public static final String COLUMN_PARENT	 			= "category_parent";
    
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
              COLUMN_ID + " int(11) NOT NULL,"+
              COLUMN_NAME +" text NOT NULL,"+
              COLUMN_PARENT +" int(11) NOT NULL,"+
			" PRIMARY KEY ("+ COLUMN_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}