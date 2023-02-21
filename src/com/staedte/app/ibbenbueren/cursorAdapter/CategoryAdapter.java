package com.staedte.app.ibbenbueren.cursorAdapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;

public class CategoryAdapter extends CursorAdapter {	
	// saved column-indexes
	private int ciID, ciName;
	
	@SuppressWarnings("deprecation")
	public CategoryAdapter(Context context, Cursor c) {
		super(context, c);
		this.ciID = c.getColumnIndex(CategoryTableInterface.COLUMN_ID);
		log("found column index of " + EntryTableInterface.COLUMN_NAME_ENTRY_ID +": "+ this.ciID);
		this.ciName = c.getColumnIndex(CategoryTableInterface.COLUMN_NAME);
		log("found column index of " + EntryTableInterface.COLUMN_NAME_TITLE +": "+ this.ciName);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {		
		TextView toptext = (TextView) view.findViewById(R.id.category_item_name);
		toptext.setText(cursor.getString(ciName));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.category_list_view_item, parent, false);
		return v;
	}
	
	public final String TAG = "CategoryAdapter";
	public void log(String msg){ Log.d(TAG, msg); }

}
