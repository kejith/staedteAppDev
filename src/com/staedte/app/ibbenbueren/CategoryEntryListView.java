package com.staedte.app.ibbenbueren;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.staedte.app.ibbenbueren.contentProvider.CategoryListProvider;
import com.staedte.app.ibbenbueren.contentProvider.EntryListProvider;
import com.staedte.app.ibbenbueren.cursorAdapter.CategoryAdapter;
import com.staedte.app.ibbenbueren.cursorAdapter.EntryAdapter;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.LabelTableInterface;

public class CategoryEntryListView extends DashboardActivity {
	
	// represents a tag which describes this class
	public final String CLASS_TAG = "CategoryEntryListView";
	
	// This is the Adapter being used to display the list's data
	EntryAdapter entryAdapter = null;
	CategoryAdapter categoryAdapter = null;
	
	// List view
	private ListView entryListView = null;
	private ListView categoryListView = null;

	// These are the Contacts rows that we will retrieve
	static final String[] CATEGORY_PROJECTION = new String[] {
		CategoryTableInterface.COLUMN_ID,
		CategoryTableInterface.COLUMN_NAME
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

		getIdFromBundle();
		initViews();

		inflateEntryListView();
		inflateCategoryListView();
	}
	
	private void initViews(){
		entryListView = (ListView) findViewById(R.id.list_data);
		categoryListView = (ListView) findViewById(R.id.category_list_data);
	}
	
	/*
	 * getIdFromBundle
	 * Restores the ID send via Bundle
	 */
	private void getIdFromBundle(){
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			int id = extras.getInt(CategoryTableInterface.COLUMN_ID);
			this.setCategoryID(id);
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg){
		Log.d(CLASS_TAG, msg);
	}
	
	public void inflateEntryListView(){
		if(this.entryAdapter != null)
			return;
		  
		EntryListProvider entryProvider = new EntryListProvider(this);
		
		Cursor cursor = null;
		cursor = entryProvider.query(EntryListProvider.LABELS_JOIN, null, null, new String[] {String.valueOf(categoryID)}, null);
		
		if(cursor != null){
			try {
				this.entryAdapter = new EntryAdapter(this, cursor);
			} catch (Exception e){
				e.printStackTrace();
			}
			
			entryListView.setAdapter(this.entryAdapter);
	
			entryListView.setOnItemClickListener(new OnItemClickListener(){		 
				public void onItemClick(AdapterView<?> parentView, View childView, int position, long id){				 
					Intent intent = new Intent(getApplicationContext(), EntryActivity.class);
					intent.putExtra(EntryTableInterface.COLUMN_ID, (long) id);
					startActivity(intent);
				 }
			});
		}
	}
	
	public void inflateCategoryListView(){
		if(this.categoryAdapter != null)
			return;
		
		CategoryListProvider provider = new CategoryListProvider(this);
		
		// get the cursor
		Cursor cursor = null;
		cursor = provider.query(CategoryListProvider.CONTENT_URI, CATEGORY_PROJECTION, null, new String[]{String.valueOf(categoryID)}, null);
		
		// get adapter
		try {
			this.categoryAdapter = new CategoryAdapter(CategoryEntryListView.this, cursor);
		} catch (Exception e){
			e.printStackTrace();
		}
		
		// set adapter
		categoryListView.setAdapter(this.categoryAdapter);
		categoryListView.setOnItemClickListener(new CategoryOnItemClickListener());
	}
	
	public void setCategoryID(int categoryID){
		this.categoryID = categoryID;
	}
	
	public class CategoryOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			Intent intent = new Intent(getApplicationContext(), CategoryEntryListView.class);
			intent.putExtra(CategoryTableInterface.COLUMN_ID, (int) id);
			startActivity(intent);
		}		
	}
}