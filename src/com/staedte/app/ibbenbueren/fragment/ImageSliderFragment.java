package com.staedte.app.ibbenbueren.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public final class ImageSliderFragment extends Fragment {
    Bundle bundle = null;
    int imageResourceID = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageView image = new ImageView(getActivity());
        image.setImageResource(getImageResourceID());
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        layout.setGravity(Gravity.CENTER);
        layout.addView(image);

        return layout;
    }
    
    public void setArguments(Bundle bundle){
    	this.bundle = bundle;
    }
    
    public Bundle getArguments(Bundle bundle){
    	return this.bundle;
    }
    
    public int getImageResourceID(){
    	return imageResourceID;
    }
    
    public void setImageResourceID(int resourceID){
    	imageResourceID = resourceID;
    }
}