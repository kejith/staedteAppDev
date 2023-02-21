package com.staedte.app.ibbenbueren.onItemClickListener;

import com.staedte.app.ibbenbueren.FullScreenViewActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.View;

class OnImageClickListener implements OnClickListener {
	 
    int _postion, parentID, parentType;
    Activity activity;

    // constructor
    public OnImageClickListener(int position, Activity activity, int parentID, int parentType) {
        this._postion = position;
        this.parentID = parentID;
        this.parentType = parentType;
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

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}

}
