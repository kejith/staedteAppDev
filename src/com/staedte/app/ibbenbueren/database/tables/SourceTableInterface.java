package com.staedte.app.ibbenbueren.database.tables;

import android.provider.BaseColumns;

public interface SourceTableInterface extends BaseColumns {
    /*
     * The Table Name
     */
	public static final String TABLE_NAME = "source";
    
    /* 
     * Columns for the table
     */
	public static final String COLUMN_ID 					= BaseColumns._ID;
	public static final String COLUMN_SOURCES_LINK			= "sources_link";
    public static final String COLUMN_SOURCES_PARENT		= "sources_parent";
    public static final String COLUMN_SOURCES_PARENT_TYPE	= "sources_parent_type";
    
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
                COLUMN_ID + " int(11),"+
                COLUMN_SOURCES_LINK + " text NOT NULL,"+
                COLUMN_SOURCES_PARENT + " int(11) NOT NULL,"+
                COLUMN_SOURCES_PARENT_TYPE + " int(11) NOT NULL,"+
			" PRIMARY KEY ("+ COLUMN_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}