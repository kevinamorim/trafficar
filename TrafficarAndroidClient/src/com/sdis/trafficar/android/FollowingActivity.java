package com.sdis.trafficar.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.sdis.trafficar.android.client.R;
import com.sdis.trafficar.helpers.*;

public class FollowingActivity extends ListActivity implements DialogInterface.OnClickListener {

	private String serviceUrl; 
	private static final String TAG = "FollowingActivity";

	private static final int GET_FOLLOWING = 0;
	private static final int UNFOLLOW = 1;

	private SharedPreferences settings;
	private String token;

	private int task = -1;
	private int idUnfollow = -1;

	@Override
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
		idUnfollow = item.getId();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_unfollow);
		builder.setPositiveButton(R.string.yes, this);
		builder.setNegativeButton(R.string.no, this);
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void handleResponse(String response) {

		try {
			
			JSONObject jso = new JSONObject(response);
			
			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");

			switch(task) {
			case GET_FOLLOWING: 

				JSONArray usersArray = jso.getJSONArray("users");
				List<UserItemAdapter> users = new ArrayList<UserItemAdapter>();
				for(int i = 0; i < usersArray.length(); i++) {
					int id = Integer.parseInt(usersArray.getJSONObject(i).getString("id"));
					String username = usersArray.getJSONObject(i).getString("username");
					String location = usersArray.getJSONObject(i).getString("location");
					users.add(new UserItemAdapter(id, username, location, true));
				}
				
				updateList(users);

				break;
			default:
				break;
			}

		} catch (JSONException e) {
			Log.e(TAG, "Error parsing JSON Object.");
			Log.e(TAG, Log.getStackTraceString(e));
		}

	}

	private void getUsers() {
		
		task = GET_FOLLOWING;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting following users...") {
			@Override
			public void onResponseReceived(String response) {
				((FollowingActivity) mContext).handleResponse(response);
			}
		};

		wst.addHeader("Authorization", token);

		String url = serviceUrl + "/Following";

		wst.execute(new String[] { url });
	}
	
	private void updateList(List<UserItemAdapter> users) {
		UserArrayAdapter adapter = new UserArrayAdapter(this, users);
		setListAdapter(adapter);
	}

	private void unfollow() {
		
		task = UNFOLLOW;
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Unfollowing user...") {
			@Override
			public void onResponseReceived(String response) {
				((FollowingActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addHeader("Authorization", token);
		wst.addParam("id", "" + idUnfollow);
		
		String url = serviceUrl + "/Unfollow";
		
		wst.execute(new String[] { url });
		
		getUsers();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which) {
		case DialogInterface.BUTTON_POSITIVE:
			unfollow();
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			break;
		default:
			break;
		}
	}

}
