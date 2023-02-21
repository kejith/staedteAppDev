package com.staedte.app.ibbenbueren;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.constants.Constants;
import com.staedte.app.ibbenbueren.contentProvider.CategoryListProvider;
import com.staedte.app.ibbenbueren.cursorAdapter.CategoryAdapter;
import com.staedte.app.ibbenbueren.database.CategoryOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.interfaces.OnReadyActivity;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.xml.GetCategoryXML;
import com.staedte.app.ibbenbueren.xml.GetEntryXML;

public class CategoryViewLoader extends Activity 
	implements OnReadyActivity<ArrayList<Entry>> {
	
	// represents a tag which describes this class
	public final String CLASS_TAG = "ListViewLoader.class";
	
	// it represents the EntryOpenHelper who can give us access to the database
	private SQLiteOpenHelper oh;
	
	// This is the Adapter being used to display the list's data
	CategoryAdapter mAdapter = null;

	// These are the Contacts rows that we will retrieve
	static final String[] PROJECTION = new String[] {
		CategoryTableInterface.COLUMN_ID,
		CategoryTableInterface.COLUMN_NAME
	};
	
	static final int[] BIND_TO = new int[] {
		R.id.toptext,
		R.id.bottomtext,
		R.id.icon
	};
	
	// List view
	private ListView listView = null;

	// This is the select criteria
	static final String SELECTION = "((" + 
			EntryTableInterface.COLUMN_NAME_TITLE + " != '') AND (" +
			EntryTableInterface.COLUMN_NAME_DESCRIPTION + " != '' ))";
	
	// ====================================================
	// ================= ON_CREATE ========================
	// ====================================================
	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.category_list_view);
		log("onCreate: instantiated");

		listView = (ListView) findViewById(R.id.list_data);
		
		this.oh = new CategoryOpenHelper(this);
    	
    	this.onReadyActivity(new ArrayList<Entry>());
	}
	
	@Override
	public void onReadyActivity(ArrayList<Entry> result) {
		CategoryListProvider provider = new CategoryListProvider(this);
		
		// get the cursor
		log("Execute: Query("+ CategoryListProvider.CONTENT_URI+", "+ Arrays.toString(PROJECTION) +", null, null, null);");
		Cursor cursor = provider.query(CategoryListProvider.CONTENT_URI, PROJECTION, null, null, null);

		// get adapter
		try {
			this.mAdapter = new CategoryAdapter(CategoryViewLoader.this, cursor);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		// set adapter
		listView.setAdapter(this.mAdapter);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), ListViewLoader.class);
				intent.putExtra(CategoryTableInterface.COLUMN_ID, (int) id);
				startActivity(intent);
			}
			
		});
	}

	private void log(String msg){ Log.d(CLASS_TAG, msg); }

}