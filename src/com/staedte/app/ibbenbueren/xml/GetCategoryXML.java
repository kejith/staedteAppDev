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

import com.staedte.app.ibbenbueren.database.CategoryOpenHelper;
import com.staedte.app.ibbenbueren.database.LabelOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;
import com.staedte.app.ibbenbueren.interfaces.OnReadyActivity;

public class GetCategoryXML extends GetXML {
	
	final String CATEGORY_ID_NAME = CategoryTableInterface.COLUMN_CATEGORY_ID;
	final String CATEGORY_NAME_NAME = CategoryTableInterface.COLUMN_NAME;
	final String CATEGORY_PARENT_NAME = CategoryTableInterface.COLUMN_PARENT;

	final String LABELS_NAME = "labels";
	final String LABEL_NAME = "label";
	final String LABEL_ID_NAME = LabelTableInterface.COLUMN_LABEL_ID;
	final String LABEL_CATEGORY_ID_NAME = LabelTableInterface.COLUMN_CATEGORY_ID;
	final String LABEL_ENTRY_ID_NAME = LabelTableInterface.COLUMN_ENTRY_ID;

	public GetCategoryXML(InputStream stream, Activity activity) {
		super(stream, activity);
	}

	public GetCategoryXML(String url, Activity activity) {
		super(url, activity);
		ParseXML p = new ParseXML();
		p.execute();
	}

	@Override
	protected Category readEntry() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "category");
		int categoryID = 0;
		int categoryParent = 0;
		String categoryName = null;
		ArrayList<Label> labels = new ArrayList<Label>();
		
		while(this.parser.next() != XmlPullParser.END_TAG){
	       //if (parser.getEventType() == XmlPullParser.START_TAG) continue;
			
	        String name = parser.getName();
	        if(name != null){
		        if(name.equals(this.CATEGORY_ID_NAME)) categoryID = readID();
		        else if(name.equals(this.CATEGORY_NAME_NAME)) categoryName = readName();
		        else if(name.equals(this.CATEGORY_PARENT_NAME)) categoryParent = readParent();
		        else if(name.equals(this.LABELS_NAME)) labels = readLabels();
	        }
	        
		}		

		parser.require(XmlPullParser.END_TAG, ns, "category");
		Category c = new Category(categoryID, categoryName, categoryParent);
		
		if(labels.size() > 0)
			c.setLabels(labels);
		
		return c;
	}
	
	protected  ArrayList<Label> readLabels() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.LABELS_NAME);
	    ArrayList<Label> l = new ArrayList<Label>();
	    
	    while(this.parser.next() != XmlPullParser.END_TAG){
	    	Label label = readLabel();
	        l.add(label);
	    }	    
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.LABELS_NAME);
	    return l;
	}
	
	protected Label readLabel() throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "label");
		int labelID = 0;
		int categoryID = 0;
		int entryID = 0;
		
		while(this.parser.next() != XmlPullParser.END_TAG){
			String name = parser.getName();
			if(name != null){
				if(name.equals(LABEL_ID_NAME)) 					labelID 	= Integer.parseInt(readText());
				else if(name.equals(LABEL_CATEGORY_ID_NAME)) 	categoryID	= Integer.parseInt(readText());
				else if(name.equals(LABEL_ENTRY_ID_NAME)) 		entryID 	= Integer.parseInt(readText());
			}
		}		

		parser.require(XmlPullParser.END_TAG, ns, "label");
		
		Label l = new Label(labelID, categoryID, entryID);
		return l;
	}
	
	protected int readInt(String requiredTag) throws XmlPullParserException, IOException {
	    parser.require(XmlPullParser.START_TAG, ns, requiredTag);
	    String unparsedText = readText();

	    int integer = 0;
	    try {
	    	integer = Integer.parseInt(unparsedText);
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	// TODO Update XML-Creation and insert default Values so we can skip this shit
	    	// But better we do some catch and try instead of letting the app crash
	    	logE("Could not parse int, maybe the value in the xml is not set. The unparsed Text was: "+ unparsedText);
	    }
	    
	    parser.require(XmlPullParser.END_TAG, ns, requiredTag);
	    return integer;
	}
	
	protected int readID() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_ID_NAME);
	    int categoryID = Integer.parseInt(readText());
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_ID_NAME);
	    return categoryID;
	}
	
	protected String readName() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_NAME_NAME);
	    String categoryName = readText();
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_NAME_NAME);
	    return categoryName;
	}
	
	protected int readParent() throws XmlPullParserException, IOException{
	    parser.require(XmlPullParser.START_TAG, ns, this.CATEGORY_PARENT_NAME);
	    
	    int categoryParent = 0;
	    try {
	    	categoryParent = Integer.parseInt(readText());
	    } catch(Exception e) {
	    	e.printStackTrace();
	    	// TODO Update XML-Creation and insert default Values so we can skip this shit
	    	// But better we do some catch and try instead of letting the app crash
	    	wtf("Could not parse int, maybe the value in the xml is not set");
	    }
	    
	    parser.require(XmlPullParser.END_TAG, ns, this.CATEGORY_PARENT_NAME);
	    return categoryParent;
	}
	
	public static class Category implements CategoryTableInterface {
		public final int categoryID;
		public final String categoryName;
		public final int categoryParent;
		public ArrayList<Label> labels = null;
		
		public Category(int categoryID, String categoryName, int categoryParent){
			this.categoryID = categoryID;
			this.categoryName = categoryName;
			this.categoryParent = categoryParent;
		}
		
		public void setLabels(ArrayList<Label> labels){
			this.labels = labels;
		}
		
		public ContentValues getValues(){
			ContentValues values = new ContentValues();

			values.put(COLUMN_ID, this.categoryID);
			values.put(COLUMN_NAME, this.categoryName);
			values.put(COLUMN_PARENT, this.categoryParent);
			
			return values;
		}
		
		@Override
		public String toString(){			
			return "Category[cID: "+ this.categoryID +"; cName: "+ this.categoryName +"; cPatent: "+ this.categoryParent +"]";
		}
	}
	
	public static class Label implements LabelTableInterface {
		public final int labelID;
		public final int categoryID;
		public final int entryID;
		
		public Label(int labelID, int categoryID, int entryID){
			this.labelID = labelID;
			this.categoryID = categoryID;
			this.entryID = entryID;
		}
		
		public ContentValues getValues(){
			ContentValues values = new ContentValues();

			values.put(COLUMN_ID, this.labelID);
			values.put(COLUMN_CATEGORY_ID, this.categoryID);
			values.put(COLUMN_ENTRY_ID, this.entryID);
			
			return values;
		}
		
		@Override
		public String toString(){
			return "Label[labelID: "+ labelID +", categoryID: "+ categoryID +",entryID: "+ entryID +"]";
		}
	}
	
    protected class ParseXML extends AsyncTask<Void, Void, ArrayList<Category>> {

        @SuppressWarnings("unchecked")
		@Override
        protected ArrayList<Category> doInBackground(Void...voids) {
        	try {
				return (ArrayList<Category>) parseXML();
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return new ArrayList<Category>();
        }        
        
        @Override
        protected void onPostExecute(ArrayList<Category> result) { 
        	// get Database
        	CategoryOpenHelper coh = new CategoryOpenHelper(activity);
        	LabelOpenHelper loh = new LabelOpenHelper(activity);
    		SQLiteDatabase db = coh.getWritableDatabase();
    		
    		// Empty the Table for the sake of laziness
    		db.delete(CategoryTableInterface.TABLE_NAME, null, null);
    		db.delete(LabelTableInterface.TABLE_NAME, null, null);
    		coh.onCreate(db);
    		db = coh.getWritableDatabase();
    		
    		long newRowID = -1;
    		if(result != null){
	    		for(Category c : (ArrayList<Category>) result){
	    			// >>> INSERT CATEGORY INTO DATABASE ===
	    			ContentValues cv = c.getValues(); 			
	    			
	    			// insert into database
	   				newRowID = db.replace(CategoryTableInterface.TABLE_NAME, null, cv);	   				
	   				
	   				// <<<< EOF INSERT CATEGORY INTO DATABASE ===
	   	    		
	   	    		// >>> INSERT LABELS INTO DATABASE ===
	   	    		if(c.labels != null){
		   	    		for(Label l : c.labels){
		   	    			cv = l.getValues();
		   	    			
		   	    			newRowID = db.replace(LabelTableInterface.TABLE_NAME, null, cv);
		   	    		}
	    			}
	   	    		// <<< EOF INSERT INTO DATABASE
	    		}
    		} else {
    			log("result is null but it should not be null");
    		}
    		
    		db.close();
        }
        
    }
    
    @Override
	protected ArrayList readFeed() throws XmlPullParserException, IOException {
    	ArrayList<Category> entries = new ArrayList<Category>();

	    this.parser.require(XmlPullParser.START_TAG, ns, "categories");
	    while (this.parser.next() != XmlPullParser.END_TAG) {
	        if (this.parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = this.parser.getName();
	        if (name.equals("category")) {
	        	
	            entries.add(readEntry());
	        } else {
	        	continue;
	        }
	    }
	    
	    return entries;
	}
    
	public String TAG = "GetCategoryXML";
	public void log(String msg) 	{ Log.d(TAG, msg); }
	public void logE(String msg) 	{ Log.e(TAG, msg); }	
	public void wtf(String msg)		{ Log.wtf(TAG, msg); }
}
