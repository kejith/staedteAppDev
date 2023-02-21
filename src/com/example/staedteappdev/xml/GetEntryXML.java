package com.example.staedteappdev.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.example.staedteappdev.database.EntryOpenHelper;
import com.example.staedteappdev.database.tables.EntryTableInterface;
import com.example.staedteappdev.interfaces.Loggable;
import com.example.staedteappdev.interfaces.OnReadyActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GetEntryXML extends GetXML implements Loggable {

	final String ID_NAME = "entry_id";
	final String TITLE_NAME = "entry_title";
	final String DESCRIPTION_NAME = "entry_description";
	final String LATITUDE_NAME = "entry_latitude";
	final String LONGITUDE_NAME = "entry_longitude";
	final String CATEGORY_ID_NAME = "entry_category_id";
	final String CATEGORY_NAME_NAME = "category_name";
	
    protected class ParseXML extends AsyncTask<Void, Void, ArrayList> {

        @Override
        protected ArrayList doInBackground(Void...voids) {
        	try {
				return parseXML();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return new ArrayList();
        }        
        
        @Override
        protected void onPostExecute(ArrayList result) { 
        	
        	// Log the Size of the result
        	log((result != null ? "Size of result: " + result.size() : "onReadyActivity -> result is null"));
    		
        	EntryOpenHelper eoh = new EntryOpenHelper(activity);        	
    		SQLiteDatabase db = eoh.getWritableDatabase();
    		
    		// Empty the Table for the sake of laziness
    		//db.delete(EntryTableInterface.TABLE_NAME, null, null);
    		//eoh.onCreate(db);
    		
    		ContentValues values = new ContentValues();
    		long newRowID = -1;
    		for(Entry e : (ArrayList<Entry>) result){
   				newRowID = db.replace(EntryTableInterface.TABLE_NAME, null, e.getValues());
   				
   				// Log if data could be inserted
   	    		log((newRowID != -1 ? "Try to insert into Database -> insert return ID: "+ newRowID : "Try to insert into Database -> insert failed or no data to be inserted"));

    		}    		    		        	
        	
        	OnReadyActivity ora = (OnReadyActivity) activity;
        	ora.onReadyActivity(result);
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
	
	public static class Entry implements EntryTableInterface {
		public final int ID;
		public final int categoryID;
		public final String categoryName;
		public final String title;
		public final String description;
		public final double latitude;
		public final double longitude;
		
		public Entry(int ID, int categoryID, String categoryName, String title, 
				String description, double latitude, double longitude){
			
			this.ID = ID;
			this.categoryID = categoryID;
			this.categoryName = categoryName;
			this.title = title;
			this.description = description;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		
		public ContentValues getValues(){
			ContentValues values = new ContentValues();
			
			values.put(COLUMN_NAME_ENTRY_ID, this.ID);
			values.put(COLUMN_NAME_TITLE, this.title);
			values.put(COLUMN_NAME_DESCRIPTION, this.description);
			values.put(COLUMN_NAME_LATITUDE, this.latitude);
			values.put(COLUMN_NAME_LONGITUDE, this.longitude);
			values.put(COLUMN_NAME_CATEGORY_ID, this.categoryID);
			
			return values;
		}
	}
	
	protected Object readEntry() throws XmlPullParserException, IOException {
		//parser.require(XmlPullParser.START_TAG, ns, "entry");
		int ID = 0;
		int categoryID = 0;
		String categoryName = null;
		String title = null;
		String description = null;
		double latitude = 0;
		double longitude = 0;
		
		while(this.parser.next() != XmlPullParser.END_TAG){
	        //if (parser.getEventType() != XmlPullParser.START_TAG) continue;

	        String name = parser.getName();
	        if(name.equals(this.ID_NAME)) ID = readID();
	        else if(name.equals(this.TITLE_NAME)) title = readTitle();
	        else if(name.equals(this.DESCRIPTION_NAME)) description = readDescription();
	        else if(name.equals(this.LATITUDE_NAME)) latitude = readLatitude();
	        else if(name.equals(this.LONGITUDE_NAME)) longitude = readLongitude();
	        else if(name.equals(this.CATEGORY_ID_NAME)) categoryID = readCategoryID();
	        else if(name.equals(this.CATEGORY_NAME_NAME)) categoryName = readCategoryName();
	        
		}		
		return new Entry(ID, categoryID, categoryName, title, description, latitude, longitude);		
	}
	
	protected String readTitle() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.TITLE_NAME);
	    String title = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.TITLE_NAME);
	    return title;
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