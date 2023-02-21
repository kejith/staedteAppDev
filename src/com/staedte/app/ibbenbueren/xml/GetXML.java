package com.staedte.app.ibbenbueren.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;

abstract class GetXML {
	protected InputStream stream = null;
	protected String url = "";
	protected XmlPullParser parser = null;
	protected ArrayList<?> elements = null;
	protected Activity activity = null;
    public ArrayAdapter<?> m_adapter = null;

		
	// so we can do an async download
	// better for network connections
    protected class DownloadXmlTask extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... urls) {
            try {
            	return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
            	e.printStackTrace();
            }
            
            return null;
        }
        
    }    
	
	// parse the xml has do be done in background
	// so we do it in background
    protected class ParseXML extends AsyncTask<Void, Void, ArrayList<?>> {

        @Override
        protected ArrayList<?> doInBackground(Void...voids) {
        	try {
				return parseXML();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return new ArrayList<Object>();
        }        
        
        @Override
        protected void onPostExecute(ArrayList<?> result) {}
        
    }
	
	// we dont use namespaces
	protected static final String ns = null;
	
	public GetXML(InputStream stream, Activity activity){
		this.setStream(stream);
		this.setActivity(activity);
	}
	
	public GetXML(String url, Activity activity){
		Log.d("GetXML", "First command in GetXml.Constructor");
		this.url = url;
		this.setActivity(activity);
		this.stream = this.getDataFromUrl();
		Log.d("GetXML", "getDataFromUrl worked, maybe - it terminated anyway");
	}

	public InputStream getStream() {
		return stream;
	}

	public void setStream(InputStream stream) {
		if(stream == null) throw new IllegalArgumentException("Can't work with an empty Stream in ParseXML.java");
		this.stream = stream;
	}
	
	protected InputStream getDataFromUrl(){
		Log.d("GetXML", "in getDataFromUrl");
		AsyncTask<String, Void, InputStream> at = new DownloadXmlTask().execute(this.url);
		Log.d("GetXML", "Url is: "+this.url);
		
		Log.d("GetXML", "Before Try and Catch-Block in getDataFromUrl");
		try {
			Log.d("GetXML", "Try and Catch works");
			InputStream is = at.get();
			Log.d("GetXML", "AsynkTask.get() went well;");
			
			if(is != null)
				return is;
			else
				Log.d("GetXML", "InputStream is null in getDataFromUrl");
			
		} catch (InterruptedException e) {
			Log.d("GetXML", "Try and Catch InterruptedException");
			e.printStackTrace();
		} catch (ExecutionException e) {
			Log.d("GetXML", "Try and Catch ExecutionException");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public ArrayList<?> getElements(){
		if(this.elements == null)
			Log.d("GetXML", "No Elements");
		
		return this.elements;
	}
	
	public void setActivity(Activity activity){
		if(activity == null) 
			throw new IllegalArgumentException("Activity is null");
		
		this.activity = activity;
	}
	
	public Activity getActivity(){
		return this.activity;
	}
	
	protected ArrayList<?> parseXML() throws XmlPullParserException, IOException {
		try {
			if(this.parser == null && this.stream != null) {
				this.parser = Xml.newPullParser();
		        this.parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		        this.parser.setInput(this.stream, null);
		        this.parser.nextTag();
		        Log.d("GetXMl", "parseXml: before calling readFead()");
		        return this.readFeed();
			}
		} finally {
			if(this.stream != null)
				this.stream.close();
		}
		
		return null;
	}
	
	protected ArrayList<Object> readFeed() throws XmlPullParserException, IOException {
	    ArrayList<Object> entries = new ArrayList<Object>();

	    this.parser.require(XmlPullParser.START_TAG, ns, "entries");
	    while (this.parser.next() != XmlPullParser.END_TAG) {
	        if (this.parser.getEventType() != XmlPullParser.START_TAG) {
	            continue;
	        }
	        String name = this.parser.getName();
	        if (name.equals("entry")) {
	            entries.add(readEntry());
	        } else {
	        	continue;
	        }
	    }
	    this.parser.require(XmlPullParser.END_TAG, ns, "entries");
	    
	    return entries;
	}
	
	protected abstract Object readEntry() throws XmlPullParserException, IOException;	
	
	protected String readText() throws IOException, XmlPullParserException {
	    String result = "";
	    if (this.parser.next() == XmlPullParser.TEXT) {
	        result = this.parser.getText();
	        this.parser.nextTag();
	    }
	    return result;
	}

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    protected InputStream loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        try {
        	InputStream is = downloadUrl(urlString);
        	
        	if(is == null)
        		Log.d("GetXML", "InputStream is null in loadXMLFromNetwork");
        	else
        		Log.d("GetXML", "InputStream is not null! YAY");
        	
            return is;
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    protected InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        Log.d("GetXML", "Url is: "+url.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        if(conn != null)
        	Log.d("GetXML", "Url Connection should be okay to this point");
        else
        	Log.d("GetXML", "Url Connection failed");
        
        // preferecnces 
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        
        // Starts the query
        conn.connect();
        
        return conn.getInputStream();
    }
}