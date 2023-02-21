package com.staedte.app.ibbenbueren.cursorAdapter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.DownloadImageTask;
import com.staedte.app.ibbenbueren.contentProvider.SourceProvider;
import com.staedte.app.ibbenbueren.database.DatabaseOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.database.tables.SourceTableInterface;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.lib.FileAdapter;
import com.staedte.app.ibbenbueren.lib.Source;

public class EntryAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	Context context = null;
	
	// saved column-indexes
	private int ciTitle, ciDescription, ciImageLink, ciEntryID;
	
	@SuppressWarnings("deprecation")
	public EntryAdapter(Context context, Cursor c) {
		super(context, c);
		
		this.context = context;

		this.inflater = LayoutInflater.from(context);
		this.ciEntryID = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_ENTRY_ID);
		this.ciTitle = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_TITLE);
		this.ciDescription = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_DESCRIPTION);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView image = (ImageView) view.findViewById(R.id.entry_item_image);
		TextView toptext = (TextView) view.findViewById(R.id.entry_item_title);
		TextView bottomtext = (TextView) view.findViewById(R.id.entry_item_description);
		
		DatabaseOpenHelper dbh = new DatabaseOpenHelper(this.context);
		SourceProvider sp = new SourceProvider(context);
		
		String title = cursor.getString(ciTitle);
		String entryID = cursor.getString(ciEntryID);
		String description = cursor.getString(ciDescription);
		
		Entry entry = new Entry(Integer.valueOf(entryID), dbh);
		entry.loadSources(sp);
		
		Log.d("EntryAdapater", Arrays.deepToString(entry.sources));
		
		if(entry.hasSources()){
			// bind image to view
			String fileName = entry.sources[0].getLink();
			int height, width;
			height = width = FileAdapter.convertDpToPixel(20, context);
			new FileAdapter().bindImageToView(fileName, image, width, height);

		}
		
		//toptext.setText(title +" "+ entryID);
		toptext.setText(title);
		bottomtext.setText(description);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return this.inflater.inflate(R.layout.entry_list_view_item, parent, false);
	}

}

//Cursor imageCursor = null;
//Uri uri = SourceProvider.CONTENT_URI;
//String[] projection = new String[]{SourceTableInterface.COLUMN_ID, SourceTableInterface.COLUMN_SOURCES_LINK};
//String selection = " "+SourceTableInterface.COLUMN_SOURCES_PARENT+"=? AND "+ SourceTableInterface.COLUMN_SOURCES_PARENT_TYPE+"=? ";
//String[] selectionArgs = new String[]{entryID, Source.ENTRY_IMAGE+""};
//String orderBy = null;
//
//try {
//	imageCursor = sp.query(uri, projection, selection, selectionArgs, orderBy);
//	Log.d("EntryAdapter", imageCursor.getCount()+"");
//} catch(IllegalArgumentException e){
//	String errorValues = String.format("uri: %s, projection: %s,.e selection: %s, selectionArgs: %s, sortOrder: %s",
//			uri.toString(), 
//			StringUtils.join(projection, ", "),
//			selection,
//			StringUtils.join(selectionArgs, ", "),
//			orderBy
//	);
//	Log.e("EntryAdapter", errorValues);
//	e.printStackTrace();
//} catch(Exception e){
//	e.printStackTrace();
//}
//
//String fileName = "";
//if(imageCursor != null && imageCursor.moveToFirst()){
//	fileName = imageCursor.getString(imageCursor.getColumnIndex(SourceTableInterface.COLUMN_SOURCES_LINK));	
//	new FileAdapter().bindImageToView(fileName, image, 100, 100);
//}
