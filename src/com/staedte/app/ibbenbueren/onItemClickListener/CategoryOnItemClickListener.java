package com.staedte.app.ibbenbueren.onItemClickListener;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.staedte.app.ibbenbueren.CategoryEntryListView;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;

public class CategoryOnItemClickListener implements OnItemClickListener {
	private Context context = null;
	
	public CategoryOnItemClickListener(Context c){
		this.context = c;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		Intent intent = new Intent(context, CategoryEntryListView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(CategoryTableInterface.COLUMN_ID, (int) id);
		context.startActivity(intent);
	}		
}
