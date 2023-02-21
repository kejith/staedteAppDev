package com.staedte.app.ibbenbueren;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

import com.staedte.app.ibbenbueren.constants.Constants;
import com.staedte.app.ibbenbueren.cursorAdapter.CursorPageAdapter;
import com.staedte.app.ibbenbueren.lib.Category;
import com.staedte.app.ibbenbueren.transformation.ZoomOutPageTransformer;
import com.staedte.app.ibbenbueren.xml.GetCategoryXML;
import com.staedte.app.ibbenbueren.xml.GetEntryXML;
import com.viewpagerindicator.TabPageIndicator;
 
public class MainActivity extends DashboardActivity implements OnClickListener {

	private Cursor categories = null;
	
	private CursorPageAdapter adapter = null;
	private ViewPager pager = null;
	private TabPageIndicator indicator = null;

	private ImageButton refresh = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main_activity); 
        
        this.initViews();
        this.initCategories();
        
        if(this.categories != null && this.categories.moveToFirst()){
        	this.initViewPager();
        } 
    }
    
    public void initViews(){
    	this.pager 		= (ViewPager)findViewById(R.id.pager);
        this.indicator 	= (TabPageIndicator)findViewById(R.id.indicator);
        
        // the refresh button
        this.refresh 	= (ImageButton)findViewById(R.id.refresh_topbar_button);
        this.refresh.setOnClickListener(this);
    }
    
    /*
     * initViewPager
     * Everything needed to get the adapter working is initialized here
     */
    public void initViewPager(){
        this.adapter = new CursorPageAdapter(this, this.categories);
        
        this.pager.setPageTransformer(true, new ZoomOutPageTransformer());
        this.pager.setAdapter(adapter);
 
        this.indicator.setViewPager(this.pager);  	
    }
    
    public void initCategories(){
        if(this.categories == null)
        	this.categories = Category.getCategoriesByParent(0, this);   	
    }
	
    /*
     * loadXmlData
     * Loads the XML-File from the Web. After downloading the file
     * it will be parsed and stored in the database
     */
    public void loadXmlData(){
    	try {
    		new GetEntryXML(Constants.ENTRY_URL, this);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    	
    	try {
    		new GetCategoryXML(Constants.CATEGORY_URL, this);
    	} catch (Exception e){
    		e.printStackTrace();
    	} 	
    }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_topbar_button:
			this.loadXmlData();
			break;

		default:
			break;
		}
	}
	
    @Override
    protected void onDestroy(){
       super.onDestroy();
       this.categories.close();
    }	
}