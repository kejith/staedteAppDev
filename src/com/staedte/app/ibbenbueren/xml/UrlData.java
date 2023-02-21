package com.staedte.app.ibbenbueren.xml;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class UrlData extends AsyncTask<String, Void, InputStream>
{
	private String url;
	private InputStream response;
	
	public UrlData(String url){
		this.setUrl(url);
		
		this.execute(url);
	}	
	
	public InputStream getUrlData(String url) throws URISyntaxException, ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet method = new HttpGet(new URI(url));
		HttpResponse res = client.execute(method);
		
		return  res.getEntity().getContent();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if(this.url == "") throw new IllegalArgumentException("No url provided in Class:UrlData");
		
		this.url = url;
	}

	public InputStream getResponse() {
		return response;
	}

	@Override
	protected InputStream doInBackground(String... url) {
		if(url.length == 1){		
			try {
				
				this.response = this.getUrlData(url[0]);
				return this.response;
				
			} catch(Exception e) {
				Log.d("UrlData->doInBackground", "Network Exception");
			}
		}
		
		return null;
	}

}