package com.sdis.trafficar.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.sdis.trafficar.android.client.R;
import com.sdis.trafficar.helpers.ServiceHelpers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	private SharedPreferences settings;

	private String serviceUrl; 
	private static final String TAG = "ProfileActivity";

	private static final int GET_PROFILE = 0;
	private static final int UPDATE_PROFILE = 1;
	private static final int GET_FOLLOWING = 2;

	private String authToken;
	private int task = -1;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		serviceUrl = ServiceHelpers.getServiceUrl(settings, "/ProfileService");
		
		getAuthentication();	
		getUserInfo();
	}

	public void saveChanges(View v) {

		task = UPDATE_PROFILE;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Updating user information...") {
			@Override
			public void onResponseReceived(String response) {
				((ProfileActivity) mContext).handleResponse(response);
			}
		};

		EditText etUsername = (EditText) findViewById(R.id.et_username);
		EditText etEmail = (EditText) findViewById(R.id.et_email);
		EditText etName = (EditText) findViewById(R.id.et_name);
		EditText etLocation = (EditText) findViewById(R.id.et_location);

		String username = etUsername.getText().toString();
		String email = etEmail.getText().toString();
		String name = etName.getText().toString();
		String location = etLocation.getText().toString();

		wst.addHeader("Authorization", authToken);
		wst.addParam("username", username);
		wst.addParam("email", email);
		wst.addParam("name", name);
		wst.addParam("location", location);

		String url =  serviceUrl + "/EditProfile";
		wst.execute(new String[] { url });

	}

	public void following(View v) {
		Intent intent = new Intent(ProfileActivity.this, FollowingActivity.class);
		startActivity(intent);
	}

	public void handleResponse(String response) {

		try {
			JSONObject jso = new JSONObject(response);

			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");

			switch(task) {
			case GET_PROFILE:

				if(success) {

					String username = jso.getString("username");
					String email = jso.getString("email");
					String name = jso.getString("name");
					String location = jso.getString("location");
					int totalPostedInfo = jso.getInt("total_information");
					int feedback = jso.getInt("feedback");

					setUserDetails(username, email, name, location, totalPostedInfo, feedback);

				} else {

					setUnauthorized();

				}


				break;

			case UPDATE_PROFILE:

				if(success) {
					Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
					startActivity(intent);
				} else {
					TextView tvMessage = (TextView) findViewById(R.id.tv_message); 
					tvMessage.setText(msg);
				}

				break;
				
			case GET_FOLLOWING: 
				break;

			default:
				break;
			}





		} catch (JSONException e) {
			System.err.println("Error creating JSON Object.");
		}
	}

	private void getUserInfo() {

		task = GET_PROFILE;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting user information...") {
			@Override
			public void onResponseReceived(String response) {
				((ProfileActivity) mContext).handleResponse(response);
			}
		};

		wst.addHeader("Authorization", authToken);
		String url =  serviceUrl + "/GetProfile";
		wst.execute(new String[] { url });
	}

	private void setUserDetails(String username, String email, String name, String location, int totalPostedInfo, int feedback) {
		EditText etUsername = (EditText) findViewById(R.id.et_username);
		EditText etEmail = (EditText) findViewById(R.id.et_email);
		EditText etName = (EditText) findViewById(R.id.et_name);
		EditText etLocation = (EditText) findViewById(R.id.et_location);
		TextView tvTotalPostedInfo = (TextView) findViewById(R.id.tv_total_information);
		TextView tvFeedback = (TextView) findViewById(R.id.tv_feedback);

		etUsername.setText(username);
		etEmail.setText(email);
		etName.setText(name);
		etLocation.setText(location);
		tvTotalPostedInfo.setText("" + totalPostedInfo);
		
		String feedbackMsg = "";
		if(feedback > 0) feedbackMsg += "+";
		feedbackMsg += feedback;
		
		tvFeedback.setText(feedbackMsg);
	}

	private void setUnauthorized() {
		setContentView(R.layout.unauthorized);
	}

	private void getAuthentication() {
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		authToken = settings.getString("token", "0");
	}

}
