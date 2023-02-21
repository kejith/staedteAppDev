package com.staedte.app.ibbenbueren;

import java.io.File;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.contentProvider.SourceProvider;
import com.staedte.app.ibbenbueren.database.EntryOpenHelper;
import com.staedte.app.ibbenbueren.database.tables.EntryTableInterface;
import com.staedte.app.ibbenbueren.lib.Address;
import com.staedte.app.ibbenbueren.lib.Entry;
import com.staedte.app.ibbenbueren.lib.FileAdapter;
import com.staedte.app.ibbenbueren.lib.Source;
import com.staedte.app.ibbenbueren.pagerAdapter.ImageSliderFragmentAdapter;
import com.viewpagerindicator.CirclePageIndicator;

public class EntryActivity extends FragmentActivity {

	private EntryOpenHelper eoh = null;

	// represents a tag which describes this class
	public final String CLASS_TAG = "EntryActivity.java";
	
	private FragmentPagerAdapter mAdapter = null;
	private ViewPager mPager = null;
	private CirclePageIndicator mIndicator = null; 
	
	// instance variables
	private long id = 0;
	private boolean hasId = false;
	
	private Entry entry = null;
	
	// views
	private TextView tvTitle 		= null;
	private TextView tvDescription 	= null;
	private TextView tvStreet 		= null;
	private TextView tvCity 		= null;
	private TextView tvCountry 		= null;
	private TextView tvPhone		= null;
	private TextView tvFax			= null;
	private TextView tvEmail		= null;
	private TextView tvWebsite		= null;
	private ImageView ivImage 		= null;
	
	private RelativeLayout addressBox 	= null;
	private RelativeLayout phoneBox 	= null;
	private RelativeLayout emailBox 	= null;
	private RelativeLayout faxBox 		= null;
	private RelativeLayout websiteBox 	= null;
	
	private Button pdfButton = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_entry);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// init everything send via bundle object
		this.initBundleVariables();
		
		// init database connection
		if(this.eoh == null)
			this.eoh = new EntryOpenHelper(this);
		
		// load everything from database related to this entry id
		this.entry = new Entry((int) id, eoh);
		this.entry.initEntryWithDatabase();
		this.entry.loadAddresses();
		this.entry.loadSources(new SourceProvider(this));
		this.entry.loadPdfs(new SourceProvider(this));
		
		// init views
		this.initViews();
		this.fillViews();	
	}
	
	private void fillViews(){
		this.setTitle(this.entry.title);
		this.tvTitle.setText(this.entry.title);
		
		this.fillAddressField();
		this.fillPhoneField();
		this.fillEmailField();
		this.fillFaxField();
		this.fillWebsiteField();
		this.fillDescriptionField();
		
		this.fillImageSlider();
		this.fillPdfButton();
	}
	
	private void fillAddressField(){
		if(this.entry.hasAddresses()){
			Address a = entry.addresses[0];
			String street = a.street +" "+ a.streetNumber;
			String city = a.city;
			String country = a.country;
			
			// fill TextViews with data
			this.tvStreet.setText(street);
			this.tvCity.setText(city);
			this.tvCountry.setText(country);
			
			// set on click listener
			addressBox.setOnClickListener(
					new AddressOnClickListener(street, city, country, this)
			);
		} else {
			// if we do not have any address remove view
			LinearLayout addressBoxParent = (LinearLayout) this.addressBox.getParent();
			addressBoxParent.removeView(addressBox);
		}
	}
	
	private void fillPhoneField(){
		String phone = this.entry.phone;
		
		if(!phone.equals("")){
			phoneBox.setOnClickListener(new PhoneOnClickListener(phone, this));
			tvPhone.setText(phone);
		} else {
			// remove view if we dont have an phone number
			RelativeLayout phoneBoxParent = (RelativeLayout) phoneBox.getParent();
			phoneBoxParent.removeView(phoneBox);
		}
	}
	
	private void fillEmailField(){
		String email = this.entry.email;
		
		if(email != null && !email.equals("")){
			emailBox.setOnClickListener(new EmailOnClickListener(new String[]{email}, this));				
			tvEmail.setText(email);
		} else {
			// remove view if we dont have an email
			LinearLayout emailBoxParent = (LinearLayout) emailBox.getParent();
			emailBoxParent.removeView(emailBox);
		}
	}
	
	private void fillFaxField(){
		String fax = this.entry.fax;
		
		if(fax != null && !fax.equals("")){
			faxBox.setOnClickListener(new PhoneOnClickListener(fax, this));				
			tvFax.setText(fax);
		} else {
			LinearLayout faxBoxParent = (LinearLayout) faxBox.getParent();
			faxBoxParent.removeView(faxBox);
		}
	}
	
	private void fillWebsiteField(){
		String website = this.entry.website;
		
		if(website != null && !website.equals("")){			
			tvWebsite.setText(website);	
			websiteBox.setOnClickListener(new WebsiteOnClickListener(website, this));
		} else {
			// remove view if we do not have a website url
			LinearLayout websiteBoxParent = (LinearLayout) websiteBox.getParent();
			websiteBoxParent.removeView(websiteBox);
		}
	}
	
	private void fillDescriptionField(){
		String  description = this.entry.description;
		tvDescription.setText(description);
	}
	
	private void fillImageSlider(){
		if(this.entry.hasSources()){
			Source mainImage = this.entry.sources[0];
			FileAdapter fa = new FileAdapter();
			
			// init image slider and view pager
			mAdapter = new ImageSliderFragmentAdapter(this.getSupportFragmentManager());
			
			// init view pager
	        mPager = (ViewPager) findViewById(R.id.pager);
	        mPager.setAdapter(mAdapter);
	
	        // init view pager indicators
	        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
	        mIndicator.setViewPager(mPager);
	        ((CirclePageIndicator) mIndicator).setSnap(true);
	        
	        // set OnClickListener to open slider in fullscreen activity
	        ivImage.setOnClickListener(new OnImageClickListener(0, EntryActivity.this, (int) id, Source.ENTRY_IMAGE));
	        
	        // set image
			String fileName = mainImage.getLink();
			fa.bindImageToView(fileName, ivImage, FileAdapter.convertDpToPixel(150, getApplicationContext()), FileAdapter.convertDpToPixel(150, this));
	        
	        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
	            @Override
	            public void onPageSelected(int position) {
	            }
	
	            @Override
	            public void onPageScrolled(int position,
	                    float positionOffset, int positionOffsetPixels) {
	            }
	
	            @Override
	            public void onPageScrollStateChanged(int state) {
	            }
	        });
		} else {
			LinearLayout imageViewBox = (LinearLayout) ivImage.getParent();
			LinearLayout mainView = (LinearLayout) imageViewBox.getParent();
			
			mainView.removeView(imageViewBox);
		}
	}
	
	private void fillPdfButton(){
		if(this.entry.hasPdfs()){
			Source pdf = this.entry.pdfs[0];
			log(String.format("PDF-Count: %d", this.entry.pdfs.length));
			log(String.format("PDF-Button: %s", this.pdfButton.toString()));
			this.pdfButton.setOnClickListener(new OnPdfButtonClickListener(pdf, this));
		} else {
			LinearLayout pdfButtonBox = (LinearLayout) pdfButton.getParent();
			pdfButtonBox.removeView(pdfButton);
		}
	}
	
	private void initBundleVariables(){
		Bundle extras = getIntent().getExtras();
		
		if(extras == null)
			return;
		
		this.id = extras.getLong(EntryTableInterface.COLUMN_ID);
		this.hasId = (id != 0);
	}
	
	private void initViews(){
		this.tvTitle 		= (TextView) findViewById(R.id.entryActivityTitle);
		this.tvDescription 	= (TextView) findViewById(R.id.descriptionEntry);
		this.tvStreet 		= (TextView) findViewById(R.id.entryActivityStreet);
		this.tvCity 		= (TextView) findViewById(R.id.entryActivityCity);
		this.tvCountry 		= (TextView) findViewById(R.id.entryActivityCountry);
		this.tvPhone		= (TextView) findViewById(R.id.entryActivityPhone);
		this.tvFax			= (TextView) findViewById(R.id.entryActivityFax);
		this.tvEmail		= (TextView) findViewById(R.id.entryActivityEmail);
		this.tvWebsite		= (TextView) findViewById(R.id.entryActivityWebsite);
		this.ivImage 		= (ImageView) findViewById(R.id.entryActivityImage);
		
		this.addressBox = (RelativeLayout) findViewById(R.id.addressBox);
		this.phoneBox = (RelativeLayout) findViewById(R.id.phoneBoxEntry);
		this.emailBox = (RelativeLayout) findViewById(R.id.emailBoxEntry);
		this.faxBox = (RelativeLayout) findViewById(R.id.faxBoxEntry);
		this.websiteBox = (RelativeLayout) findViewById(R.id.websiteBoxEntry);
		
		this.pdfButton = (Button) findViewById(R.id.pdfButton);
	}
	
	public class AddressOnClickListener implements OnClickListener {
		private String address = "";
		private Context activity = null;
		
		public AddressOnClickListener(String street, String city, String country, FragmentActivity activity){
			this.address = street +"+"+ city +"+"+country;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			Intent intent = null;	
			String url = "geo:0,0?q="+address.replace(" ", "%20");
			intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
			intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
			this.activity.startActivity(intent);		
		}		
	}
	
	public class PhoneOnClickListener implements OnClickListener {
		private String phone = "";
		private Context activity = null;
		
		public PhoneOnClickListener(String phone, FragmentActivity activity){
			this.phone = phone;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {
			try {
		        Intent intent = new Intent(Intent.ACTION_CALL);
		        intent.setData(Uri.parse("tel:"+ phone));
			    activity.startActivity(intent);
			} catch (ActivityNotFoundException e) {
			    Log.e("dialing example", "Call failed", e);
			}		
		}		
	}

	public class EmailOnClickListener implements OnClickListener {
		private String[] email = null;
		private Context activity = null;
		
		public EmailOnClickListener(String email[], FragmentActivity activity){
			this.email = email;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {			
			try {
				final Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_EMAIL, this.email);
				activity.startActivity(Intent.createChooser(intent, "Email versenden"));
			} catch (ActivityNotFoundException e) {
			    Log.e("", "Call failed", e);
			}		
		}		
	}

	public class WebsiteOnClickListener implements OnClickListener {
		private String url = null;
		private Context activity = null;
		
		public WebsiteOnClickListener(String url, FragmentActivity activity){
			if (!url.startsWith("http://") && !url.startsWith("https://"))
				   url = "http://" + url;
			
			this.url = url;
			this.activity = activity;
		}

		@Override
		public void onClick(View v) {			
			try {
				Log.d("Url:", this.url);
				final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.url));
				activity.startActivity(Intent.createChooser(browserIntent, "Homepage öffnen:"));
			} catch (ActivityNotFoundException e) {
			    Log.e("", "Call failed", e);
			}		
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entry, menu);
		return true;
	}

	@SuppressWarnings("unused")
	private void log(String msg){
		Log.d(CLASS_TAG, msg);
	}
	
	public void goHome(Context context) {
	    final Intent intent = new Intent(context, HomeActivity.class);
	    intent.setFlags (Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    context.startActivity (intent);
	}
	
	class OnImageClickListener implements OnClickListener {
		 
	    int _postion, parentID, parentType;
	    Activity activity;

	    // constructor
	    public OnImageClickListener(int position, Activity activity, int parentID, int parentType) {
	        this._postion = position;
	        this.parentID = parentID;
	        this.parentType = parentType;
	        this.activity = activity;
	    }

	    public void onClick(View v) {
	        // on selecting grid view image
	        // launch full screen activity
	        Intent i = new Intent(activity, FullScreenViewActivity.class);
	        i.putExtra("position", _postion);
	        i.putExtra("parentID", parentID);
	        i.putExtra("parentType", parentType);
	        activity.startActivity(i);
	    }

	}
	
	class OnPdfButtonClickListener implements OnClickListener {
	    Activity activity;
	    Source pdf = null;

	    // constructor
	    public OnPdfButtonClickListener(Source pdf, Activity activity) {
	    	this.pdf = pdf;
	        this.activity = activity;
	    }

	    public void onClick(View v) {
	    	// download the pdf file
	    	String path = this.pdf.download(pdf.getLink(), null);
	    	
	        Intent i = new Intent(activity, PdfViewer.class);
	        i.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
	        activity.startActivity(i);
	    }

	}
}
