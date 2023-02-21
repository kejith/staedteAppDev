package com.staedte.app.ibbenbueren;
 
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.staedte.app.ibbenbueren.constants.Constants;
import com.staedte.app.ibbenbueren.lib.ConnectionDetector;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.xml.GetCategoryXML;
import com.staedte.app.ibbenbueren.xml.GetEntryXML;
 
public class SplashScreen extends Activity {
 
    // splash screen time
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        int count = Entry.getEntriesCount(this);

        ConnectionDetector connectionDetector = new ConnectionDetector(this);
        if(count < 1 && connectionDetector.isConnectedToTheInternet()){
        	this.loadXmlData();
        }
    	
    	// init splash screen and delay it
        new Handler().postDelayed(new Runnable() { 
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
        
    }
    
    public void loadXmlData(){
        // ==== Load and Parse XML-Files
        // entry xml
    	GetEntryXML entryXML;
    	try {
    		new GetEntryXML(Constants.ENTRY_URL, this);
    	} catch (XmlPullParserException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	// category xml
    	GetCategoryXML categoryXML;
    	try {
    		new GetCategoryXML(Constants.CATEGORY_URL, this);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	// === EOF Loading and Parsing XML-Files   	
    }
 
}