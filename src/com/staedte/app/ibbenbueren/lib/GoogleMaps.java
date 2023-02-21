package com.staedte.app.ibbenbueren.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class GoogleMaps {
	
	private static double lng = 0;
	private static double lat = 0;
	

	public GoogleMaps() {
	}
	
	public void getLatLongFromAddress(String address) {

		String url = "http://maps.google.com/maps/api/geocode/json?address=" +address+"&sensor=false";
		try {
			url = URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			url = "http://maps.google.com/maps/api/geocode/json?address=&sensor=false";
		}
        HttpGet httpGet = new HttpGet(url);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        } catch (IllegalStateException e){
        }

        JSONObject jsonObject = new JSONObject();
        
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                .getJSONObject("geometry").getJSONObject("location")
                .getDouble("lat");

            Log.d("latitude",""+ lat);
            Log.d("longitude",""+ lng);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
	
	public String getStringLatLongFromAddress(String address){
		this.getLatLongFromAddress(address);
		return this.lat +","+ this.lng;
	}
	

}
