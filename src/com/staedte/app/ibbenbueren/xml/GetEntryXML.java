package com.staedte.app.ibbenbueren.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.AddressTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;
import com.staedte.app.ibbenbueren.interfaces.Loggable;
import com.staedte.app.ibbenbueren.lib.Address;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.lib.Source;

public class GetEntryXML extends GetXML implements Loggable {

	final String ID_NAME = "entry_id";
	final String TITLE_NAME = "entry_title";
	final String DESCRIPTION_NAME = "entry_description";
	final String LATITUDE_NAME = "entry_latitude";
	final String LONGITUDE_NAME = "entry_longitude";
	final String CATEGORY_ID_NAME = "entry_category";
	final String CATEGORY_NAME_NAME = "category_name";
	final String SOURCES_NAME = "sources";
	final String SOURCE_NAME = "sources_link";
	final String SOURCE_LINK_NAME = "sources_link";
	final String SOURCE_ID_NAME = "source_id";
	final String ADDRESSES_NAME = "addresses";
	final String ADDRESS_NAME = "address";
	final String PDFS_NAME = "pdfs";
	final String PDF_NAME = "pdf";
	final String PDF_LINK = "pdfs_link";
	final String PHONE_NAME = EntryTableInterface.COLUMN_NAME_PHONE;
	final String FAX_NAME = EntryTableInterface.COLUMN_NAME_FAX;
	final String EMAIL_NAME = EntryTableInterface.COLUMN_NAME_EMAIL;
	final String WEBSITE_NAME = EntryTableInterface.COLUMN_NAME_WEBSITE;
	
	
    protected class ParseXML extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(Void...voids) {
        	try {
				return parseXML();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return new ArrayList();
        }        
        
        @Override
        protected void onPostExecute(ArrayList result) { 
    		
    		// get database
    		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(activity);
    		openHelper.recreateTables();
    		
    		SQLiteDatabase db = openHelper.getWritableDatabase();
    		
    		ContentValues values = new ContentValues();
    		long newRowID = -1;
    		if(result != null){
	    		for(Entry e : (ArrayList<Entry>) result){
	    			// === INSERT ENTRY INTO DATABASE ===
	    			ContentValues cv = e.getValues();
	   				newRowID = db.replaceOrThrow(EntryTableInterface.TABLE_NAME, null, cv);
	   	    		
	   	    		// === INSERT ADDRESSES INTO DATABASE ===	
	   	    		Address[] addresses = e.getAddresses();
	   	    		if(e.hasAddresses()){	   	    			
	   	    			for(int i = 0; i < addresses.length; i++){
	   	    				db.replaceOrThrow(AddressTableInterface.TABLE_NAME, null, addresses[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		Source[] sources = e.getSources();
	   	    		if(e.hasSources()){
	   	    			for(int i = 0; i < sources.length; i++){
	   	    				sources[i].setParentID(e.ID);
	   	    				sources[i].setParentType(Source.ENTRY_IMAGE);
	   	    				db.replaceOrThrow(SourceTableInterface.TABLE_NAME, null, sources[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		if(e.hasPdfs()){
	   	    			Source[] pdfs = e.getPdfs();
	   	    			for(int i = 0; i < pdfs.length; i++){
	   	    				pdfs[i].setParentID(e.ID);
	   	    				db.replaceOrThrow(SourceTableInterface.TABLE_NAME, null, pdfs[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		
	   	    		// EOF INSERT ADDRESSES INTO DATABASE ===
	    		}
    		}
    		
    		db.close();
        }
        
    }
	
	// === CONSTRUCTORS ====
	public GetEntryXML(InputStream stream, Activity activity) {
		super(stream, activity);
	}

	public GetEntryXML(String url, Activity activity) throws XmlPullParserException, IOException{
		super(url, activity);	
		new ParseXML().execute();	
	}
	// === EOF CONSTRUCTORS ===
	
	protected Object readEntry() throws XmlPullParserException, IOException {
		//parser.require(XmlPullParser.START_TAG, ns, "entries");
		
		int ID = 0;
		int categoryID = 0;
		String categoryName = null;
		String title = null;
		String description = null;
		String[] sourceLink = null;
		String phone = null;
		String fax = null;
		String email = null;
		String website = null;
		double latitude = 0;
		double longitude = 0;
		Address[] addresses = null;
		Source[] sources = null;
		Source[] pdfs = null;
		
		while(this.parser.next() != XmlPullParser.END_TAG){
	        //if (parser.getEventType() == XmlPullParser.START_TAG) continue;
	        String name = parser.getName();
	        
	        if(name != null){
		        if(name.equals(this.ID_NAME)) ID = readID();
		        else if(name.equals(this.TITLE_NAME)) title = readTitle();
		        else if(name.equals(this.DESCRIPTION_NAME)) description = readDescription();
		        else if(name.equals(this.LATITUDE_NAME)) latitude = readLatitude();
		        else if(name.equals(this.LONGITUDE_NAME)) longitude = readLongitude();
		        else if(name.equals(this.CATEGORY_ID_NAME)) categoryID = readCategoryID();
		        else if(name.equals(this.CATEGORY_NAME_NAME)) categoryName = readCategoryName();
		        else if(name.equals(this.SOURCES_NAME)) sources = readSources();
		        else if(name.equals(this.ADDRESSES_NAME)) addresses = readAddresses();
		        else if(name.equals(this.PHONE_NAME)) phone = readText();
		        else if(name.equals(this.FAX_NAME)) fax = readText();
		        else if(name.equals(this.EMAIL_NAME)) email = readText();
		        else if(name.equals(this.WEBSITE_NAME)) website = readText();
		        else if(name.equals(this.PDFS_NAME)) pdfs = readPdfs();
	        }
	        
		}	
		Entry e = new Entry(ID, categoryID, categoryName, title, description, latitude, longitude, phone, fax, email, website);
		e.setAddresses(addresses);
		e.setSources(sources);
		e.setPdfs(pdfs);
		
		return e;
	}
	
	protected String readTitle() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.TITLE_NAME);
	    String title = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.TITLE_NAME);
	    return title;
	}
	
/*	protected String[] readSourceLinks() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);	    
	    int eventType = parser.next();
	    
	    if(eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("source")){
	    	
	    }
	    
	    ArrayList<String> al = new ArrayList<String>();
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	String s = readText();
	    	Log.d("readSourceLinks", s);
	    	al.add(s);
	    }
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
	    return al.toArray(new String[0]);
	}	*/
	
	
	protected String[] readSourceLinks() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);
	    ArrayList<String> al = new ArrayList<String>();
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	String s = readText();
	    	al.add(s);
	    }
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
	    return al.toArray(new String[0]);
	}	
	
	protected Source[] readSources() throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);
		ArrayList<Source> sources = new ArrayList<Source>();
		
		while(this.parser.next() != XmlPullParser.END_TAG){
			sources.add(readSource());
		}
		
		parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
		
		return sources.toArray(new Source[0]);
	}
	
	protected Source readSource() throws XmlPullParserException, IOException{
		
		Source source = null;
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCE_LINK_NAME);
    	source = new Source(readText());
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCE_LINK_NAME);
    	
	    return source;
	}	
	
	protected Address[] readAddresses() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.ADDRESSES_NAME);	    
	    ArrayList<Address> al = new ArrayList<Address>();
	    
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	Address address = null;
	    	address = readAddress();
		    
	        al.add(address);
	    }	    
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.ADDRESSES_NAME);
	    return al.toArray(new Address[0]);
	}
	
	protected Address readAddress() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, this.ADDRESS_NAME);
		
		Address address = new Address();
	    while(this.parser.next() != XmlPullParser.END_TAG){

	        String name = parser.getName();
	        if(name.equals("addresses_id")) 				address.ID				= Integer.parseInt(readText());
	        else if(name.equals("addresses_title")) 		address.title 			= readText();
	        else if(name.equals("addresses_street")) 		address.street			= readText();
	        else if(name.equals("addresses_street_number")) address.streetNumber 	= readText();
	        else if(name.equals("addresses_zipcode")) 		address.zipcode 		= Integer.parseInt(readText());
	        else if(name.equals("addresses_city")) 			address.city			= readText();
	        else if(name.equals("addresses_country")) 		address.country		 	= readText();
	        else if(name.equals("addresses_entry_id")) 		address.entryId		 	= Integer.parseInt(readText());
	    }	    
	    parser.require(XmlPullParser.END_TAG, ns, this.ADDRESS_NAME);
	    
	    return address;
	}
	
	protected Source[] readPdfs() throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, this.PDFS_NAME);
		ArrayList<Source> pdfs = new ArrayList<Source>();
		
		while(this.parser.next() != XmlPullParser.END_TAG){
			pdfs.add(readPDF());
		}
		
		parser.require(XmlPullParser.END_TAG, ns, this.PDFS_NAME);
		
		return pdfs.toArray(new Source[0]);
	}
		
	protected Source readPDF() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, this.PDF_NAME);
		
		Source pdf = new Source();
	    while(this.parser.next() != XmlPullParser.END_TAG){

	        String name = parser.getName();
	        if(name.equals(this.PDF_LINK)){
	        	pdf.setLink(readText());
	        	pdf.setParentType(Source.PDF);
	        }
	    }
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.PDF_NAME);
	    
	    return pdf;
	}
	
	protected String readDescription() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.DESCRIPTION_NAME);
	    String description = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.DESCRIPTION_NAME);
	    return description;
	}
	
	protected int readID() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.ID_NAME);
	    String idStr = readText();
	    int ID = Integer.parseInt(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.ID_NAME);
	    return ID;
	}
	
	protected int readCategoryID() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_ID_NAME);
	    String idStr = readText();
	    int ID = Integer.parseInt(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_ID_NAME);
	    return ID;
	}
	
	protected String readCategoryName() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_NAME_NAME);
	    String categoryName = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_NAME_NAME);
	    return categoryName;
	}
	
	protected double readLatitude() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.LATITUDE_NAME);
	    String idStr = readText();
	    double ID = Double.parseDouble(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.LATITUDE_NAME);
	    return ID;
	}
	
	protected double readLongitude() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.LONGITUDE_NAME);
	    String idStr = readText();
	    double ID = Double.parseDouble(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.LONGITUDE_NAME);
	    return ID;
	}

	public String TAG = "GetEntryXML";
	@Override
	public void log(String msg) {
		Log.d(TAG, msg);		
	}
}

/*package com.staedte.app.ibbenbueren.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.AddressTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;
import com.staedte.app.ibbenbueren.interfaces.Loggable;
import com.staedte.app.ibbenbueren.lib.Address;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.lib.Source;

public class GetEntryXML extends GetXML implements Loggable {

	final String ID_NAME = "entry_id";
	final String TITLE_NAME = "entry_title";
	final String DESCRIPTION_NAME = "entry_description";
	final String LATITUDE_NAME = "entry_latitude";
	final String LONGITUDE_NAME = "entry_longitude";
	final String CATEGORY_ID_NAME = "entry_category";
	final String CATEGORY_NAME_NAME = "category_name";
	final String SOURCES_NAME = "sources";
	final String SOURCE_NAME = "sources_link";
	final String SOURCE_LINK_NAME = "sources_link";
	final String SOURCE_ID_NAME = "source_id";
	final String ADDRESSES_NAME = "addresses";
	final String ADDRESS_NAME = "address";
	final String PDFS_NAME = "pdfs";
	final String PDF_NAME = "pdf";
	final String PDF_LINK = "pdfs_link";
	final String PHONE_NAME = EntryTableInterface.COLUMN_NAME_PHONE;
	final String FAX_NAME = EntryTableInterface.COLUMN_NAME_FAX;
	final String EMAIL_NAME = EntryTableInterface.COLUMN_NAME_EMAIL;
	final String WEBSITE_NAME = EntryTableInterface.COLUMN_NAME_WEBSITE;
	
	public long parseStartTime = 0;
	public long parseEndTime = 0;
	
	
    protected class ParseXML extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(Void...voids) {
        	try {
				return parseXML();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return new ArrayList();
        }        
        
        @Override
        protected void onPostExecute(ArrayList result) { 
    		
    		// get database
    		DatabaseOpenHelper openHelper = new DatabaseOpenHelper(activity);
    		openHelper.recreateTables();
    		openHelper.recreateTable(SourceTableInterface.TABLE_NAME, SourceTableInterface.TABLE_CREATE);
    		
    		SQLiteDatabase db = openHelper.getWritableDatabase();
    		
    		String addressSql = String.format(
    				"INSERT INTO %s (%s,%s,%s,%s,%s,%s,%s) VALUES ", 
    				AddressTableInterface.TABLE_NAME,
    				AddressTableInterface.COLUMN_NAME_COUNTRY,
    				AddressTableInterface.COLUMN_NAME_TITLE,
    				AddressTableInterface.COLUMN_NAME_STREET,
    				AddressTableInterface.COLUMN_NAME_STREET_NUMBER,
    				AddressTableInterface.COLUMN_NAME_ZIPCODE,
    				AddressTableInterface.COLUMN_NAME_CITY,
    				AddressTableInterface.COLUMN_NAME_ENTRY_ID);
    		
    		String sourceSql = String.format(
    				"INSERT INTO %s (%s, %s, %s) VALUES ", 
    				SourceTableInterface.TABLE_NAME,
    				SourceTableInterface.COLUMN_SOURCES_LINK,
    				SourceTableInterface.COLUMN_SOURCES_PARENT,
    				SourceTableInterface.COLUMN_SOURCES_PARENT_TYPE);
    		
    		String pdfSql = String.format(
    				"INSERT INTO %s (%s, %s, %s) VALUES ", 
    				SourceTableInterface.TABLE_NAME,
    				SourceTableInterface.COLUMN_SOURCES_LINK,
    				SourceTableInterface.COLUMN_SOURCES_PARENT,
    				SourceTableInterface.COLUMN_SOURCES_PARENT_TYPE);
    		
    		if(result != null){
	    		for(Entry e : (ArrayList<Entry>) result){
	    			long newRowID = -1;
	    			
	    			// === INSERT ENTRY INTO DATABASE ===
	    			ContentValues cv = e.getValues();
	   				newRowID = db.replaceOrThrow(EntryTableInterface.TABLE_NAME, null, cv);
	   				
	   	    		// === INSERT ADDRESSES INTO DATABASE ===	
	   	    		Address[] addresses = e.getAddresses();
	   	    		String[] addressColumns = {AddressTableInterface.COLUMN_NAME_TITLE,
	   	    				AddressTableInterface.COLUMN_NAME_STREET,
	   	    				AddressTableInterface.COLUMN_NAME_STREET_NUMBER,
	   	    				AddressTableInterface.COLUMN_NAME_ZIPCODE,
	   	    				AddressTableInterface.COLUMN_NAME_CITY,
	   	    				AddressTableInterface.COLUMN_NAME_COUNTRY,
	   	    				AddressTableInterface.COLUMN_NAME_ENTRY_ID};
	   	    		
	   	    		if(e.hasAddresses()){	   	    			
	   	    			for(int i = 0; i < addresses.length; i++){
	   	    				String values = "";
   	    					ContentValues addressValues = addresses[i].getValues();
   	    					
   	    					String sql = "('%s','%s',%d, %d, '%s', 'Deutschland', %d),";
	   	    				for(String columns : addressColumns){
	   	    					sql = String.format(sql, addressValues.get(columns));
	   	    				}
	   	    				
	   	    				log(sql);
	   	    				//db.replaceOrThrow(AddressTableInterface.TABLE_NAME, null, addresses[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		Source[] sources = e.getSources();
	   	    		if(e.hasSources()){
	   	    			for(int i = 0; i < sources.length; i++){
	   	    				//sources[i].setParentID(e.ID);
	   	    				//sources[i].setParentType(Source.ENTRY_IMAGE);
	   	    				//db.replaceOrThrow(SourceTableInterface.TABLE_NAME, null, sources[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		if(e.hasPdfs()){
	   	    			Source[] pdfs = e.getPdfs();
	   	    			for(int i = 0; i < pdfs.length; i++){
	   	    				//pdfs[i].setParentID(e.ID);
	   	    				//db.replaceOrThrow(SourceTableInterface.TABLE_NAME, null, pdfs[i].getValues());
	   	    			}
	   	    		}
	   	    		
	   	    		
	   	    		// EOF INSERT ADDRESSES INTO DATABASE ===
	    		}
    		}
    		
    		db.close();
    		
    		GetEntryXML.this.parseEndTime = System.nanoTime();
    		long parsingTime = GetEntryXML.this.parseEndTime - GetEntryXML.this.parseStartTime;
    		
    		log(String.format("Parsingtime: %s", parsingTime));
        }
        
    }
	
	// === CONSTRUCTORS ====
	public GetEntryXML(InputStream stream, Activity activity) {
		super(stream, activity);
		
		this.parseStartTime = System.nanoTime();
	}

	public GetEntryXML(String url, Activity activity) throws XmlPullParserException, IOException{
		super(url, activity);	
		this.parseStartTime = System.nanoTime();
		
		new ParseXML().execute();
	}
	// === EOF CONSTRUCTORS ===
	
	protected Object readEntry() throws XmlPullParserException, IOException {
		//parser.require(XmlPullParser.START_TAG, ns, "entries");
		
		int ID = 0;
		int categoryID = 0;
		String categoryName = null;
		String title = null;
		String description = null;
		String[] sourceLink = null;
		String phone = null;
		String fax = null;
		String email = null;
		String website = null;
		double latitude = 0;
		double longitude = 0;
		Address[] addresses = null;
		Source[] sources = null;
		Source[] pdfs = null;
		
		while(this.parser.next() != XmlPullParser.END_TAG){
	        //if (parser.getEventType() == XmlPullParser.START_TAG) continue;
	        String name = parser.getName();
	        
	        if(name != null){
		        if(name.equals(this.ID_NAME)) ID = readID();
		        else if(name.equals(this.TITLE_NAME)) title = readTitle();
		        else if(name.equals(this.DESCRIPTION_NAME)) description = readDescription();
		        else if(name.equals(this.LATITUDE_NAME)) latitude = readLatitude();
		        else if(name.equals(this.LONGITUDE_NAME)) longitude = readLongitude();
		        else if(name.equals(this.CATEGORY_ID_NAME)) categoryID = readCategoryID();
		        else if(name.equals(this.CATEGORY_NAME_NAME)) categoryName = readCategoryName();
		        else if(name.equals(this.SOURCES_NAME)) sources = readSources();
		        else if(name.equals(this.ADDRESSES_NAME)) addresses = readAddresses();
		        else if(name.equals(this.PHONE_NAME)) phone = readText();
		        else if(name.equals(this.FAX_NAME)) fax = readText();
		        else if(name.equals(this.EMAIL_NAME)) email = readText();
		        else if(name.equals(this.WEBSITE_NAME)) website = readText();
		        else if(name.equals(this.PDFS_NAME)) pdfs = readPdfs();
	        }
	        
		}	
		Entry e = new Entry(ID, categoryID, categoryName, title, description, latitude, longitude, phone, fax, email, website);
		e.setAddresses(addresses);
		e.setSources(sources);
		e.setPdfs(pdfs);
		
		return e;
	}
	
	protected String readTitle() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.TITLE_NAME);
	    String title = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.TITLE_NAME);
	    return title;
	}
	
	protected String[] readSourceLinks() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);	    
	    int eventType = parser.next();
	    
	    if(eventType == XmlPullParser.START_TAG && parser.getName().equalsIgnoreCase("source")){
	    	
	    }
	    
	    ArrayList<String> al = new ArrayList<String>();
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	String s = readText();
	    	Log.d("readSourceLinks", s);
	    	al.add(s);
	    }
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
	    return al.toArray(new String[0]);
	}	
	
	
	protected String[] readSourceLinks() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);
	    ArrayList<String> al = new ArrayList<String>();
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	String s = readText();
	    	al.add(s);
	    }
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
	    return al.toArray(new String[0]);
	}	
	
	protected Source[] readSources() throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, this.SOURCES_NAME);
		ArrayList<Source> sources = new ArrayList<Source>();
		
		while(this.parser.next() != XmlPullParser.END_TAG){
			sources.add(readSource());
		}
		
		parser.require(XmlPullParser.END_TAG, ns, this.SOURCES_NAME);
		
		return sources.toArray(new Source[0]);
	}
	
	protected Source readSource() throws XmlPullParserException, IOException{
		
		Source source = null;
	    parser.require(XmlPullParser.START_TAG, ns, this.SOURCE_LINK_NAME);
    	source = new Source(readText());
	    parser.require(XmlPullParser.END_TAG, ns, this.SOURCE_LINK_NAME);
    	
	    return source;
	}	
	
	protected Address[] readAddresses() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.ADDRESSES_NAME);	    
	    ArrayList<Address> al = new ArrayList<Address>();
	    
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	Address address = null;
	    	address = readAddress();
		    
	        al.add(address);
	    }	    
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.ADDRESSES_NAME);
	    return al.toArray(new Address[0]);
	}
	
	protected Address readAddress() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, this.ADDRESS_NAME);
		
		Address address = new Address();
	    while(this.parser.next() != XmlPullParser.END_TAG){

	        String name = parser.getName();
	        if(name.equals("addresses_id")) 				address.ID				= Integer.parseInt(readText());
	        else if(name.equals("addresses_title")) 		address.title 			= readText();
	        else if(name.equals("addresses_street")) 		address.street			= readText();
	        else if(name.equals("addresses_street_number")) address.streetNumber 	= readText();
	        else if(name.equals("addresses_zipcode")) 		address.zipcode 		= Integer.parseInt(readText());
	        else if(name.equals("addresses_city")) 			address.city			= readText();
	        else if(name.equals("addresses_country")) 		address.country		 	= readText();
	        else if(name.equals("addresses_entry_id")) 		address.entryId		 	= Integer.parseInt(readText());
	    }	    
	    parser.require(XmlPullParser.END_TAG, ns, this.ADDRESS_NAME);
	    
	    return address;
	}
	
	protected Source[] readPdfs() throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, this.PDFS_NAME);
		ArrayList<Source> pdfs = new ArrayList<Source>();
		
		while(this.parser.next() != XmlPullParser.END_TAG){
			pdfs.add(readPDF());
		}
		
		parser.require(XmlPullParser.END_TAG, ns, this.PDFS_NAME);
		
		return pdfs.toArray(new Source[0]);
	}
		
	protected Source readPDF() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, this.PDF_NAME);
		
		Source pdf = new Source();
	    while(this.parser.next() != XmlPullParser.END_TAG){

	        String name = parser.getName();
	        if(name.equals(this.PDF_LINK)){
	        	pdf.setLink(readText());
	        	pdf.setParentType(Source.PDF);
	        }
	    }
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.PDF_NAME);
	    
	    return pdf;
	}
	
	protected String readDescription() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.DESCRIPTION_NAME);
	    String description = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.DESCRIPTION_NAME);
	    return description;
	}
	
	protected int readID() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.ID_NAME);
	    String idStr = readText();
	    int ID = Integer.parseInt(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.ID_NAME);
	    return ID;
	}
	
	protected int readCategoryID() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_ID_NAME);
	    String idStr = readText();
	    int ID = Integer.parseInt(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_ID_NAME);
	    return ID;
	}
	
	protected String readCategoryName() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_NAME_NAME);
	    String categoryName = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_NAME_NAME);
	    return categoryName;
	}
	
	protected double readLatitude() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.LATITUDE_NAME);
	    String idStr = readText();
	    double ID = Double.parseDouble(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.LATITUDE_NAME);
	    return ID;
	}
	
	protected double readLongitude() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.LONGITUDE_NAME);
	    String idStr = readText();
	    double ID = Double.parseDouble(idStr);
	    parser.require(XmlPullParser.END_TAG, ns, this.LONGITUDE_NAME);
	    return ID;
	}

	public String TAG = "GetEntryXML";
	@Override
	public void log(String msg) {
		Log.d(TAG, msg);		
	}
}*/