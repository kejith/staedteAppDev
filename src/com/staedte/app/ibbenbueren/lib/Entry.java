package com.staedte.app.ibbenbueren.lib;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.staedte.app.ibbenbueren.contentProvider.SourceProvider;
import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.AddressTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;

public class Entry implements EntryTableInterface {
	public int ID 			= 0;
	public int categoryID 	= 0;
	
	public String categoryName 	= "";
	public String title 		= " ";
	public String description 	= "";
	public String phone 		= "";
	public String fax 			= "";
	public String email 		= "";
	public String website 		= "";
	
	public double latitude 		= 0;
	public double longitude 	= 0;	
	
	public Address[] addresses = null;
	public Source[] sources = null;
	public Source[] pdfs = null;
	
	private SQLiteOpenHelper eoh = null;
	
	public Entry(int ID, int categoryID, String categoryName, String title, 
		String description, double latitude, double longitude,  String phone, 
		String fax, String email, String website)
	{
		this.ID = ID;
		this.categoryID = categoryID;
		this.categoryName = categoryName;
		this.title = title;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
		this.website = website;
	}
	
	public Entry(int ID, SQLiteOpenHelper eoh){
		this.ID = ID;
		this.eoh = eoh;
	}
	
	public boolean initEntryWithDatabase(){
		if(this.eoh == null)
			return false;
		
		boolean worked = false;
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			db = this.eoh.getReadableDatabase();
			c = db.rawQuery("SELECT * FROM " + EntryTableInterface.TABLE_NAME + " WHERE entry_id = " + this.ID, null);
			
			int colTitle, colDescription, colPhone, colFax, colEmail, colWebsite;
			colTitle = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_TITLE);
			colDescription = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_DESCRIPTION);
			colPhone = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_PHONE);
			colFax = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_FAX);
			colEmail = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_EMAIL);
			colWebsite = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_WEBSITE);
			
			if(c.moveToFirst()){
				this.title = c.getString(colTitle);
				this.description = c.getString(colDescription);
				this.phone = c.getString(colPhone);
				this.fax = c.getString(colFax);
				this.email = c.getString(colEmail);
				this.website = c.getString(colWebsite);
				
				worked = true;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(c != null)
				c.close();
			
			if(db != null)
				db.close();
		}
		
		return worked;
	}
	
	public ContentValues getValues(){
		ContentValues values = new ContentValues();

		Log.d("Entry", String.format("Entry::getValues() entryTitle: %s", this.title));
		
		values.put(COLUMN_ID, this.ID);
		values.put("entry_id", this.ID);
		values.put(COLUMN_NAME_TITLE, this.title);
		values.put(COLUMN_NAME_DESCRIPTION, this.description);
		values.put(COLUMN_NAME_LATITUDE, this.latitude);
		values.put(COLUMN_NAME_LONGITUDE, this.longitude);
		values.put(COLUMN_NAME_CATEGORY_ID, this.categoryID);
		
		if(this.phone != null)
			values.put(COLUMN_NAME_PHONE, this.phone);
		
		if(this.email != null)
			values.put(COLUMN_NAME_EMAIL, this.email);

		if(this.fax != null)
			values.put(COLUMN_NAME_FAX, this.fax);

		if(this.website != null)
			values.put(COLUMN_NAME_WEBSITE, this.website);
		
		return values;
	}
	
	public void loadAddresses(){
		ArrayList<Address> addressList = new ArrayList<Address>();
		SQLiteDatabase db = this.eoh.getReadableDatabase();
		
		Cursor addressCursor = null;
		try {
			addressCursor = db.rawQuery("SELECT * FROM "+ AddressTableInterface.TABLE_NAME +" WHERE addresses_entry_id = "+ this.ID, null);
			
			if(!addressCursor.moveToFirst())
				return;
			
			int colTitle, colStreet, colStreetNumber, colZipcode, colCity, colCountry;
			colTitle 		= addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_TITLE);
			colStreet 		= addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_STREET);
			colStreetNumber = addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_STREET_NUMBER);
			colZipcode 		= addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_ZIPCODE);
			colCity 		= addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_CITY);
			colCountry	 	= addressCursor.getColumnIndex(AddressTableInterface.COLUMN_NAME_COUNTRY);
			
			do {
				Address a = new Address();
				
				// set everything
				a.setTitle(addressCursor.getString(colTitle));
				a.setStreet(addressCursor.getString(colStreet));
				a.setStreetNumber(addressCursor.getString(colStreetNumber));
				a.setZipcode(addressCursor.getInt(colZipcode));
				a.setCity(addressCursor.getString(colCity));
				a.setCountry(addressCursor.getString(colCountry));
				
				// add to list
				addressList.add(a);
			} while(addressCursor.moveToNext());
			
			this.setAddresses(addressList.toArray(new Address[0]));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(addressCursor != null)
				addressCursor.close();			
			
			if(db != null)
				db.close();
		}
	}
	
	public void loadSources(SourceProvider sourceProvider){
		ArrayList<Source> sourceList = new ArrayList<Source>();
		
		Cursor cursor = null;
		try {
			// create sql statement to load sources from database
			cursor = sourceProvider.getCursorOfSourcesByID(this.ID);
			
			// get column indexes
			int colLink = cursor.getColumnIndex(SourceTableInterface.COLUMN_SOURCES_LINK);
			
			while(cursor.moveToNext()) {
				Source source = new Source();
				source.setLink(cursor.getString(colLink));
				sourceList.add(source);
			} 
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(cursor != null)
				cursor.close();
		}
		
		this.setSources(sourceList.toArray(new Source[0]));
	}
	
	public void loadPdfs(SourceProvider sourceProvider){
		ArrayList<Source> pdfList = new ArrayList<Source>();
		
		Cursor pdfCursor = null;
		try {
			Uri contentUri 			= SourceProvider.CONTENT_URI;
			String[] projection 	= new String[]{SourceTableInterface.COLUMN_ID, SourceTableInterface.COLUMN_SOURCES_LINK};
			String selection 		= String.format(" %s = ? AND %s = ?", SourceTableInterface.COLUMN_SOURCES_PARENT, SourceTableInterface.COLUMN_SOURCES_PARENT_TYPE);
			String[] selectionArgs	= new String[]{String.valueOf(ID), String.valueOf(Source.PDF)}; 
			
			pdfCursor = sourceProvider.query(contentUri, projection, selection, selectionArgs, null);
			
			Log.d("Entry.java", String.format("Pdf-Cursor-Count: %d", pdfCursor.getCount()));
			
			if(pdfCursor == null || !pdfCursor.moveToFirst())
				return;;
			
			int columnIndexLink = pdfCursor.getColumnIndex(SourceTableInterface.COLUMN_SOURCES_LINK);
			
			do {
				Source pdf = new Source();
				
				// set everything
				pdf.setLink(pdfCursor.getString(columnIndexLink));
				
				// add to list
				pdfList.add(pdf);
			} while(pdfCursor.moveToNext());
			
			// set pdfs in entry
			this.setPdfs(pdfList.toArray(new Source[0]));
		} catch(Exception e) {
			// print StackTrace for debugging purpose
			e.printStackTrace();
		} finally {
			if(pdfCursor != null)
				pdfCursor.close();
		}
	}
	
	public void setAddresses(Address[] addresses){
		if(addresses != null && addresses.length > 0)
			this.addresses = addresses;
	}
	
	public void setSources(Source[] sources){
		if(sources != null && sources.length != 0)
			this.sources = sources;
	}
	
	public void setPdfs(Source[] pdfs){
		this.pdfs = pdfs;
	}
	
	public Address[] getAddresses(){
		return this.addresses;
	}
	
	public Source[] getSources(){
		return this.sources;
	}
	
	public Source[] getPdfs(){
		return this.pdfs;
	}
	
	public boolean hasAddresses(){
		return (this.addresses != null && this.addresses.length > 0);
	}
	
	public boolean hasSources(){
		return (this.sources != null && this.sources.length > 0);
	}
	
	public boolean hasPdfs(){
		return (this.pdfs != null && this.pdfs.length > 0);
	}
	
	@Override
	public String toString(){
		return "[ID: "+ this.ID +"][Title: "+ this.title +"][ID: "+ this.ID+"]";
	}
	
	public static int getEntriesCount(Context context){
		String sql = String.format("SELECT * FROM %s", EntryTableInterface.TABLE_NAME);
		int count = 0;
	
		DatabaseOpenHelper dbOpenHelper = new DatabaseOpenHelper(context);
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		
		
		// exec sql query and return cursor with count
		Cursor c = db.rawQuery(sql, null);
		count = c.getCount();
		
		// close everything
		c.close();
		db.close();
		
		return count;
	}
}
