package com.sdis.trafficar.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sdis.trafficar.helpers.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

public class ExploreActivity extends ListActivity {

	private String serviceUrl; 
	private static final String TAG = "ExploreActivity";

	private static final int GET_USERS_TASK = 0;
	private static final int FOLLOW_USER_TASK = 1;

	private SharedPreferences settings; 

	private String token;

	private int task = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		settings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		serviceUrl = ServiceHelpers.getServiceUrl(settings, "/UserService");
		token = settings.getString(Constants.TOKEN_SETTINGS_NAME, Constants.TOKEN_DEF_VALUE);
		getUsers();
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		UserItemAdapter item = (UserItemAdapter) getListAdapter().getItem(position);
		follow(item.getId());
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
				
			case FOLLOW_USER_TASK:
				
				if(success) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage("FOLLOWING!").setTitle("Follow User");
					AlertDialog dialog = builder.create();
					dialog.show();
				} else {
					
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

		String url = serviceUrl + "/GetUsers";
		wst.execute(new String[] { url });

	}
	
	private void updateUsersList(ArrayList<Integer> ids, ArrayList<String> usernames, ArrayList<String> locations) {
		ArrayList<UserItemAdapter> items = new ArrayList<UserItemAdapter>();
		for(int i = 0; i < ids.size(); i++) {
			UserItemAdapter item = new UserItemAdapter(ids.get(i), usernames.get(i), locations.get(i), false);
			items.add(item);
		}
		
		UserArrayAdapter adapter = new UserArrayAdapter(this, items);
		setListAdapter(adapter);
	}
	
	private void follow(int id) {
		
		task = FOLLOW_USER_TASK;
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Following user...") {
			@Override
			public void onResponseReceived(String response) {
				((ExploreActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addHeader("Authorization", token);
		wst.addParam("id", "" + id);
		
		String url = serviceUrl + "/FollowUser";
		wst.execute(new String[] { url });
		
	}
	
}
