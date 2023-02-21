package com.staedte.app.ibbenbueren.database.tables;

import android.provider.BaseColumns;

public interface LabelTableInterface extends BaseColumns {
    /*
     * The Table Name
     */
	public static final String TABLE_NAME = "label";
    
    /* 
     * Columns for the table
     */
	public static final String COLUMN_ID 					= BaseColumns._ID;
	public static final String COLUMN_LABEL_ID				= "label_id";
	public static final String COLUMN_CATEGORY_ID			= "label_category_id";
    public static final String COLUMN_ENTRY_ID	 			= "label_entry_id";
    
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
                COLUMN_ID + " int(11) NOT NULL,"+
                COLUMN_CATEGORY_ID + " int(11) NOT NULL,"+
                COLUMN_ENTRY_ID + " int(11) NOT NULL,"+
			" PRIMARY KEY ("+ COLUMN_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}