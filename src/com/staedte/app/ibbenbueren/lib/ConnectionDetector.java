package com.staedte.app.ibbenbueren.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
	
	private static final int NO_NETWORK_CONNECTED = -1;
	
	private Context context;
	
	
	
	public ConnectionDetector(Context context) {
		this.context = context;
	}
	
	public boolean isConnectedToTheInternet(){
		ConnectivityManager cm = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(cm != null){
			NetworkInfo[] info = cm.getAllNetworkInfo();
			
			int connectedNetwork = this.isAnyNetworkConnected(info);
			return (connectedNetwork != NO_NETWORK_CONNECTED);
		}
		
		return false;
	}
	
	public int isAnyNetworkConnected(NetworkInfo[] info){
		if(info == null)
			return -1;
		
		for(int i = 0; i < info.length; i++){
			if(info[i].getState() == NetworkInfo.State.CONNECTED){
				return i;
			}
		}
		
		return -1;
	}

}
