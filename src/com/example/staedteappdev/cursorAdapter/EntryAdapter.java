package com.example.staedteappdev.cursorAdapter;

import java.io.InputStream;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.staedteappdev.R;
import com.example.staedteappdev.database.tables.EntryTableInterface;

public class EntryAdapter extends CursorAdapter {

	private LayoutInflater inflater;
	
	// saved column-indexes
	private int ciTitle, ciDescription;
	
	@SuppressWarnings("deprecation")
	public EntryAdapter(Context context, Cursor c) {
		super(context, c);
		Log.d("EntryAdapter","super constructor worked");
		this.inflater = LayoutInflater.from(context);
		Log.d("EntryAdapter","layout inflater worked");
		this.ciTitle = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_TITLE);
		Log.d("EntryAdapter","found column index of " + EntryTableInterface.COLUMN_NAME_TITLE +": "+ this.ciTitle);
		this.ciDescription = c.getColumnIndex(EntryTableInterface.COLUMN_NAME_DESCRIPTION);
		Log.d("EntryAdapter","found column index of " + EntryTableInterface.COLUMN_NAME_DESCRIPTION +": "+ this.ciDescription);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView image = (ImageView) view.findViewById(R.id.icon);
		
		new DownloadImageTask(image).execute("http://www.klosterhausbuch.de/online/templatemedia/all_lang/resources/grimm-hans.jpg");
		
		TextView toptext = (TextView) view.findViewById(R.id.toptext);
		TextView bottomtext = (TextView) view.findViewById(R.id.bottomtext);
		
		toptext.setText(cursor.getString(ciTitle));
		bottomtext.setText(cursor.getString(ciDescription));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return this.inflater.inflate(R.layout.activity_main, null);
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	        bmImage.setImageBitmap(result);
	        
//	        int height = bmImage.getLayoutParams().height;
//	        int width = bmImage.getLayoutParams().width;
	        
//	        if(height > width){
//	        	bmImage.setMaxHeight(72);
//	        } else {
//	        	bmImage.setMaxWidth(72);
//	        }
	    }
	}

}
