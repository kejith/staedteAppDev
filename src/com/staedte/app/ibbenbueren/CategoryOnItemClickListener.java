package com.staedte.app.ibbenbueren;

import com.staedte.app.ibbenbueren.CategoryEntryListView;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class CategoryOnItemClickListener implements OnItemClickListener {
	private Context context = null;
	
	public CategoryOnItemClickListener(Context context){
		this.context = context;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent intent = new Intent(context, CategoryEntryListView.class);
		intent.putExtra(CategoryTableInterface.COLUMN_ID, (int) id);
		context.startActivity(intent);
	}		
}	