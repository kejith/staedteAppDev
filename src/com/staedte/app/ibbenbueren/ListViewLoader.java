package com.staedte.app.ibbenbueren;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.constants.Constants;
import com.staedte.app.ibbenbueren.contentProvider.EntryListProvider;
import com.staedte.app.ibbenbueren.cursorAdapter.EntryAdapter;
import com.staedte.app.ibbenbueren.database.EntryOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;
import com.staedte.app.ibbenbueren.interfaces.OnReadyActivity;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.xml.GetEntryXML;

public class ListViewLoader extends Activity 
	implements OnReadyActivity<ArrayList<Entry>> {
	
	// represents a tag which describes this class
	public final String CLASS_TAG = "ListViewLoader.class";
	
	// it represents the EntryOpenHelper who can give us access to the database
	private EntryOpenHelper eoh;
	
	// This is the Adapter being used to display the list's data
	EntryAdapter mAdapter = null;
	
	// List view
	private ListView listView = null;

	// These are the Contacts rows that we will retrieve
	static final String[] ENTRY_PROJECTION = new String[] {
		EntryTableInterface.COLUMN_ID,
		EntryTableInterface.COLUMN_NAME_ENTRY_ID,
		EntryTableInterface.COLUMN_NAME_TITLE,
		EntryTableInterface.COLUMN_NAME_DESCRIPTION,
		EntryTableInterface.COLUMN_NAME_IMAGE_LINK
	};
	
	static final String[] LABEL_PROJECTION = new String[] {
		LabelTableInterface.COLUMN_ID,
		LabelTableInterface.COLUMN_CATEGORY_ID,
		LabelTableInterface.COLUMN_ENTRY_ID
	};

	private int categoryID = 0;
	
	// ====================================================
	// ================= ON_CREATE ========================
	// ====================================================
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_list_view);
		log("onCreate: instantiated");

		Bundle extras = getIntent().getExtras();
		if(extras != null)
			categoryID = extras.getInt(CategoryTableInterface.COLUMN_ID);

		listView = (ListView) findViewById(R.id.list_data);
			
		this.eoh = new EntryOpenHelper(this);		
    	// log if EntryOpenHelper could be instantiated 
		log("EntryOpenHelper was declared");
    	log((this.eoh == null ? "EntryOpenHelper could not be instaniated" : "EntryOpenHelper was instantiated"));
		
		log("Before Try-And-Catch block of GetEntryXML():");

		@SuppressWarnings("unused")
		GetEntryXML entryXML;
		try {
			entryXML = new GetEntryXML(Constants.ENTRY_URL, this);
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
		this.onReadyActivity(new ArrayList<Entry>());
	}

	private void log(String msg){
		Log.d(CLASS_TAG, msg);
	}

	@Override
	public void onReadyActivity(ArrayList<Entry> result) {
		EntryListProvider entryProvider = new EntryListProvider(this);
		
		Cursor cursor = null;
		if(categoryID != 0)
			cursor = entryProvider.query(EntryListProvider.LABELS_JOIN, null, null, new String[] {String.valueOf(categoryID)}, null);
		else
			cursor = entryProvider.query(EntryListProvider.LABELS_JOIN, null, null, new String[] {String.valueOf(categoryID)}, null);
		
		try {
			this.mAdapter = new EntryAdapter(this, cursor);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		listView.setAdapter(this.mAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){		 
			public void onItemClick(AdapterView<?> parentView, View childView, int position, long id){				 
				Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
				intent.putExtra(EntryTableInterface.COLUMN_ID, (long) id);
				startActivity(intent);
			 }
		});
	}
}