package com.staedte.app.ibbenbueren.lib;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

public final class PlaceSlideFragment extends Fragment {
    int imageResourceId;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    
    public void setImageResourceID(int imageResourceID){
    	this.imageResourceId = imageResourceID;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        ImageView image = new ImageView(getActivity());
        image.setImageResource(imageResourceId);

        LinearLayout layout = new LinearLayout(getActivity());
        layout.setLayoutParams(new LinearLayout.LayoutParams(800, 800));

        layout.setGravity(Gravity.CENTER);
        layout.addView(image);

        return layout;
    }
}	