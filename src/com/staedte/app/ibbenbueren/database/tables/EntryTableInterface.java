package com.staedte.app.ibbenbueren.database.tables;

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
    public static final String COLUMN_NAME_ENTRY_ID = "_id";
    public static final String COLUMN_NAME_TITLE = "entry_title";
    public static final String COLUMN_NAME_DESCRIPTION = "entry_description";
    public static final String COLUMN_NAME_LATITUDE = "enty_latitude";
    public static final String COLUMN_NAME_LONGITUDE = "entry_longitude";
    public static final String COLUMN_NAME_CATEGORY_ID = "entry_category_id";
    public static final String COLUMN_NAME_IMAGE_LINK = "image_link";
    public static final String COLUMN_NAME_PHONE = "entry_phone";
    public static final String COLUMN_NAME_FAX = "entry_fax";
    public static final String COLUMN_NAME_EMAIL = "entry_email";
    public static final String COLUMN_NAME_WEBSITE = "entry_website";
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
              COLUMN_ID + ","+
              "entry_id int(11) NOT NULL,"+
			  COLUMN_NAME_TITLE +" text NOT NULL,"+
			  COLUMN_NAME_DESCRIPTION +" blob,"+
			  COLUMN_NAME_LATITUDE +" double,"+
			  COLUMN_NAME_LONGITUDE +" double,"+
			  COLUMN_NAME_CATEGORY_ID +" int(11),"+
			  COLUMN_NAME_PHONE +" text,"+
			  COLUMN_NAME_FAX +" text,"+
			  COLUMN_NAME_EMAIL +" text,"+
			  COLUMN_NAME_WEBSITE +" text,"+
			" PRIMARY KEY ("+ COLUMN_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}