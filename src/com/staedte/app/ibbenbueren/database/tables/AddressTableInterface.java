package com.staedte.app.ibbenbueren.database.tables;

import android.provider.BaseColumns;

public interface AddressTableInterface extends BaseColumns {
    /*
     * The Table Name
     */
	public static final String TABLE_NAME = "addresses";
    
    /* 
     * Columns for the table
     */
	public static final String COLUMN_ID 					= BaseColumns._ID;
    public static final String COLUMN_NAME_TITLE 			= "addresses_title";
    public static final String COLUMN_NAME_STREET 			= "addresses_street";
    public static final String COLUMN_NAME_STREET_NUMBER 	= "addresses_street_number";
    public static final String COLUMN_NAME_ZIPCODE 			= "addresses_zipcode";
    public static final String COLUMN_NAME_CITY 			= "addresses_city";
    public static final String COLUMN_NAME_COUNTRY 			= "addresses_country";
    public static final String COLUMN_NAME_ENTRY_ID 		= "addresses_entry_id";
    
    /*
     * The SQL Create Statement for the Table
     */
    public static final String TABLE_CREATE = 
            "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (" +
              COLUMN_ID + ","+
			  COLUMN_NAME_TITLE +" text NOT NULL,"+
			  COLUMN_NAME_STREET +" text NOT NULL,"+
			  COLUMN_NAME_STREET_NUMBER +" int(5) NOT NULL,"+
			  COLUMN_NAME_ZIPCODE +" int(8) NOT NULL,"+
			  COLUMN_NAME_CITY +" text NOT NULL,"+
			  COLUMN_NAME_COUNTRY +" int(3) NOT NULL,"+
			  COLUMN_NAME_ENTRY_ID +" int(11) NOT NULL,"+
			" PRIMARY KEY ("+ COLUMN_ID +")"+
			")";
    
    /*
     * The SQL Statement to delete the Table
     */
    public static final String TABLE_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;
}