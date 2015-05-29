package com.sdis.trafficar.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdis.trafficar.helpers.*;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class ExploreActivity extends ListActivity {

	private static final String SERVICE_URL = Constants.BASE_URL + "/UserService";
	private static final String TAG = "ExploreActivity";

	private static final int GET_USERS_TASK = 0;

	private SharedPreferences settings; 

	private String token;

	private int task = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		token = settings.getString("token", "0");
		getUsers();
	}

	protected void handleResponse(String response) {

		try {

			JSONObject jso = new JSONObject(response);

			Log.d(TAG, response);

			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");

			switch(task) {
			case GET_USERS_TASK:
				
				JSONArray jsoIds = jso.getJSONArray("ids");
				JSONArray jsoUsernames = jso.getJSONArray("usernames");
				JSONArray jsoLocations = jso.getJSONArray("locations");
				
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ArrayList<String> usernames = new ArrayList<String>();
				ArrayList<String> locations = new ArrayList<String>();
				
				if((jsoIds.length() == jsoUsernames.length()
						&& (jsoIds.length() == jsoLocations.length()))) {
					for(int i = 0; i < jsoIds.length(); i++) {
						ids.add(jsoIds.getInt(i));
						usernames.add(jsoUsernames.getString(i));
						locations.add(jsoLocations.getString(i));
					}
					
					updateUsersList(ids, usernames, locations);
				}

				break;
			default:
				break;
			}

		} catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON.");
		}



	}

	private void getUsers() {

		task = GET_USERS_TASK;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting users...") {
			@Override
			public void onResponseReceived(String response) {
				((ExploreActivity) mContext).handleResponse(response);
			}
		};

		wst.addHeader("Authorization", token);

		String url = SERVICE_URL + "/GetUsers";
		wst.execute(new String[] { url });

	}
	
	private void updateUsersList(ArrayList<Integer> ids, ArrayList<String> usernames, ArrayList<String> locations) {
		ArrayList<UserItemAdapter> items = new ArrayList<UserItemAdapter>();
		for(int i = 0; i < ids.size(); i++) {
			UserItemAdapter item = new UserItemAdapter(ids.get(i), usernames.get(i), locations.get(i));
			items.add(item);
		}
		
		UserArrayAdapter adapter = new UserArrayAdapter(this, items);
		setListAdapter(adapter);
	}




}
