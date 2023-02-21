package com.staedte.app.ibbenbueren;

import java.util.ArrayList;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.contentProvider.SourceProvider;
import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.lib.FileAdapter;
import com.staedte.app.ibbenbueren.lib.Source;
import com.staedte.app.ibbenbueren.pagerAdapter.FullScreenImageAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.database.Cursor;

public class FullScreenViewActivity extends Activity {
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	private Entry entry;
	
	private int position;
	private int parentID;
	private int parentType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		// try to get extra content
		this.initExtras();
		
		// create entry object and load data
		this.entry = new Entry(parentID, new DatabaseOpenHelper(this));
		this.entry.loadSources(new SourceProvider(this));
		
		// loop through sources to get filenames
		ArrayList<String> filenames = new ArrayList<String>();
		
		for(Source source : this.entry.getSources())
			filenames.add(source.getLink());
		
		adapter = new FullScreenImageAdapter(this, filenames);
		viewPager.setAdapter(adapter);

		// displaying selected image first
		viewPager.setCurrentItem(position);
	}
	
	private void initExtras(){
		Bundle extras = getIntent().getExtras();
		
		// exit if there is no extra data
		if(extras == null)
			this.finish();
		
		this.position = extras.getInt("position", 0);
		this.parentID = extras.getInt("parentID", 0);
		this.parentType = extras.getInt("parentType", 0);
	}
}
