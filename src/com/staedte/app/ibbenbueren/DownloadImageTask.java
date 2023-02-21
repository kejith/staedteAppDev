package com.staedte.app.ibbenbueren;
import java.io.InputStream;

import com.staedte.app.ibbenbueren.lib.FileAdapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

	public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage = null;
	    String fileName = null;

	    public DownloadImageTask(ImageView bmImage, String fileName) {
	        this.bmImage = bmImage;
	        this.fileName = fileName;
	    }
	    
	    public DownloadImageTask(String fileName){
	    	this.fileName = fileName;
	    }

	    protected Bitmap doInBackground(String... urls) {
	    	String url = urls[0];
	        Bitmap b = null;
	        try {
	            InputStream in = new java.net.URL(url).openStream();
	            b = BitmapFactory.decodeStream(in);           
	            
	    		new FileAdapter().createExternalPicture(b, fileName);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return b;
	    }

	    protected void onPostExecute(Bitmap image) {
	    	if(image != null && bmImage != null){
	    		bmImage.setImageBitmap(image);
	    	}
	    }
	}