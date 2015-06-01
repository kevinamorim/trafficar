package com.sdis.trafficar.android;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.CallbackManager;
import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	private SharedPreferences settings;

	private static final String SERVICE_URL = Constants.BASE_URL + "/ProfileService";
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

		String url =  SERVICE_URL + "/EditProfile";
		wst.execute(new String[] { url });

	}

	public void following(View v) {

		task = GET_FOLLOWING;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting users that you are following...") {
			@Override
			public void onResponseReceived(String response) {
				((ProfileActivity) mContext).handleResponse(response);
			}
		};

		wst.addHeader("Authorization", authToken);
		String url = Constants.BASE_URL + "/UserService" + "/Following";
		wst.execute(new String[] { url });
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

					setUserDetails(username, email, name, location);

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
		String url =  SERVICE_URL + "/GetProfile";
		wst.execute(new String[] { url });
	}

	private void setUserDetails(String username, String email, String name, String location) {
		EditText etUsername = (EditText) findViewById(R.id.et_username);
		EditText etEmail = (EditText) findViewById(R.id.et_email);
		EditText etName = (EditText) findViewById(R.id.et_name);
		EditText etLocation = (EditText) findViewById(R.id.et_location);

		etUsername.setText(username);
		etEmail.setText(email);
		etName.setText(name);
		etLocation.setText(location);
	}

	private void setUnauthorized() {
		setContentView(R.layout.unauthorized);
	}

	private void getAuthentication() {
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		authToken = settings.getString("token", "0");
	}

}
