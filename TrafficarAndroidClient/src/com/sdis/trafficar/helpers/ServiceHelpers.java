package com.sdis.trafficar.helpers;

import com.sdis.trafficar.android.Constants;

import android.content.SharedPreferences;

public class ServiceHelpers {
	
	public static String getServiceUrl(SharedPreferences prefs, String service) {
		
		String ip = prefs.getString(Constants.HOST_IP_SETTINGS_NAME, Constants.HOST_IP_DEF_VALUE);
		String port = prefs.getString(Constants.HOST_PORT_SETTINGS_NAME, Constants.HOST_PORT_DEF_VALUE);
		
		return Constants.PROTOCOL + ip + ":" + port + Constants.BASE_URL + service;
		
	}

}
