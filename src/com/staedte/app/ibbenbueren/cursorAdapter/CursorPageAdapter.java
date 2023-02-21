package com.staedte.app.ibbenbueren.cursorAdapter;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.CategoryEntryListView;
import com.staedte.app.ibbenbueren.contentProvider.CategoryListProvider;
import com.staedte.app.ibbenbueren.database.tables.CategoryTableInterface;
import com.staedte.app.ibbenbueren.lib.CursorPagerAdapter;
import com.staedte.app.ibbenbueren.onItemClickListener.CategoryOnItemClickListener;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CursorPageAdapter extends CursorPagerAdapter {

	public int[] images = new int[] {
			R.drawable.photo1, 
			R.drawable.photo2, 
			R.drawable.photo3, 
			R.drawable.photo4,
			R.drawable.photo5, 
			R.drawable.photo6, 
			R.drawable.photo7, 
			R.drawable.photo8, 
			R.drawable.photo9
	};

	public CursorPageAdapter(Context context, Cursor c) {
		super(context, c);
	}        
	
	@Override
    public void destroyItem(View pager, int position, Object view) {
        ((ViewPager) pager).removeView((View) view);
    }

    @Override
    public void finishUpdate(View view) {
    }

    @Override
    public void restoreState(Parcelable p, ClassLoader c) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View view) {
    }
	
    public CharSequence getPageTitle(int position) {
    	mCursor.moveToPosition(position);
    	return mCursor.getString(mCursor.getColumnIndex(CategoryTableInterface.COLUMN_NAME));
    }

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public boolean isViewFromObject(View v, Object o) {
        return v.equals(o);
    }
	
    @Override
    public int getCount() {
        return mCursor.getCount();
    }
    
    public Object instantiateItem(View pager, int position) {
    	mCursor.moveToPosition(position);
    	int categoryID = mCursor.getInt(mCursor.getColumnIndex(CategoryTableInterface.COLUMN_ID));

        final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_test, null, false);
        
        ImageView image = (ImageView) v.findViewById(R.id.mainCategoryImage);
        image.setImageResource(images[position % images.length]);

        // get ListViews to be filled by the adapter
		ListView categoryListView = (ListView) v.findViewById(R.id.category_list_data);
		
		CategoryListProvider provider = new CategoryListProvider(mContext);
		
		// get the cursor
		String[] CATEGORY_PROJECTION = new String[] {
				CategoryTableInterface.COLUMN_ID,
				CategoryTableInterface.COLUMN_NAME
		};
		
		Cursor cursor = null;
		cursor = provider.query(CategoryListProvider.CONTENT_URI, CATEGORY_PROJECTION, null, new String[]{String.valueOf(categoryID)}, null);

		// get adapter
		CategoryAdapter categoryAdapter = null;
		try {
			categoryAdapter = new CategoryAdapter(mContext, cursor);
		} catch (Exception e){
			Log.e("CategoryAdapter", "Could not be instantiated");
			e.printStackTrace();
		}
		
		// set adapter
		categoryListView.setAdapter(categoryAdapter);
		categoryListView.setOnItemClickListener(new CategoryOnItemClickListener(mContext));       

        //This is very important
        ((ViewPager) pager).addView( v, 0 );

        return v;
    }
} 