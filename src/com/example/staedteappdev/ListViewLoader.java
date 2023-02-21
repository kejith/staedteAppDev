package com.example.staedteappdev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.example.staedteappdev.contentProvider.EntryListProvider;
import com.example.staedteappdev.cursorAdapter.EntryAdapter;
import com.example.staedteappdev.database.EntryOpenHelper;
import com.example.staedteappdev.database.tables.EntryTableInterface;
import com.example.staedteappdev.interfaces.OnReadyActivity;
import com.example.staedteappdev.xml.GetEntryXML;

public class ListViewLoader extends ListActivity 
	implements OnReadyActivity<ArrayList<GetEntryXML.Entry>> {
	
	// represents a tag which describes this class
	public final String CLASS_TAG = "ListViewLoader.class";
	
	// it represents the EntryOpenHelper who can give us access to the database
	private EntryOpenHelper eoh;
	
	// This is the Adapter being used to display the list's data
	EntryAdapter mAdapter = null;

	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {
		EntryTableInterface.COLUMN_ID,
		EntryTableInterface.COLUMN_NAME_TITLE,
		EntryTableInterface.COLUMN_NAME_DESCRIPTION
	};
	
	static final int[] BIND_TO = new int[] {
		R.id.toptext,
		R.id.bottomtext
	};

	// This is the select criteria
	static final String SELECTION = "((" + 
			EntryTableInterface.COLUMN_NAME_TITLE + " != '') AND (" +
			EntryTableInterface.COLUMN_NAME_DESCRIPTION + " != '' ))";
	
	// ====================================================
	// ================= ON_CREATE ========================
	// ====================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		log("onCreate: instantiated");

		this.eoh = new EntryOpenHelper(this);		
    	// log if EntryOpenHelper could be instantiated 
		log("EntryOpenHelper was declared");
    	log((this.eoh == null ? "EntryOpenHelper could not be instaniated" : "EntryOpenHelper was instantiated"));
		
		log("Before Try-And-Catch block of GetEntryXML():");
		
		@SuppressWarnings("unused")
		GetEntryXML entryXML;
		try {
			log("Try-Block: Works");
			String url = "http://www.nordhorner-karneval.de/test.xml";
			entryXML = new GetEntryXML(url, this);
		} catch (XmlPullParserException e) {
			log("XmlPullParserException");
			e.printStackTrace();
		} catch (IOException e) {
			log("IOException");
			e.printStackTrace();
		} catch (Exception e){
			log("Uncaught Exception");
			e.printStackTrace();
		}
		
		log("Instantiating: EntryListProvider");
		EntryListProvider provider = new EntryListProvider(this);
		
		log("Execute: Query("+ EntryListProvider.CONTENT_URI+", "+ Arrays.toString(PROJECTION) +", null, null, null);");
		Cursor cursor = provider.query(EntryListProvider.CONTENT_URI, PROJECTION, null, null, null);
		
		log("Instantiating: EntryAdaper");
		try {
			this.mAdapter = new EntryAdapter(this, cursor);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		log("Execute: setListAdapter");
		setListAdapter(this.mAdapter);
	}

	private void log(String msg){
		Log.d(CLASS_TAG, msg);
	}

	@Override
	public void onReadyActivity(ArrayList<GetEntryXML.Entry> result) {
		this.mAdapter.notifyDataSetChanged();
	}
}