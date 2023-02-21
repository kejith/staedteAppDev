package com.staedte.app.ibbenbueren.pagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.staedte.app.ibbenbueren.R;
import com.staedte.app.ibbenbueren.fragment.ImageSliderFragment;
import com.viewpagerindicator.IconPagerAdapter;

public class ImageSliderFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter{

    private int[] Images = new int[] { R.drawable.photo1, R.drawable.photo2,
            R.drawable.photo2, R.drawable.photo4

    };

    protected static final int[] ICONS = new int[] { R.drawable.marker,
            R.drawable.marker, R.drawable.marker, R.drawable.marker };

    private int mCount = Images.length;

    public ImageSliderFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
    	ImageSliderFragment psf = new ImageSliderFragment();
        psf.setImageResourceID(Images[position]);
        return (Fragment) psf;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public int getIconResId(int index) {
        return ICONS[index % ICONS.length];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}
