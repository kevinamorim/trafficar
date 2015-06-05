package com.sdis.trafficar.android;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sdis.trafficar.android.client.R;
import com.sdis.trafficar.helpers.ServiceHelpers;
import com.sdis.trafficar.helpers.SimpleAlertDialog;

public class AuthenticationActivity extends Activity {

	private SharedPreferences settings;

	private String serviceUrl;
	private static final String TAG = "AuthenticationActivity";

	private static final int LOGIN = 0;
	private static final int REGISTER = 1;
	private static final int CHECK = 2;
	private static final int AUTHENTICATE = 3;
	private static final int LOGIN_FB = 4;

	private int task = -1;

	private CallbackManager callbackManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.main);
		
		initSharedPrefs();
		serviceUrl = ServiceHelpers.getServiceUrl(settings, "/MembershipService");
		setAddress();
		setFacebookLogin();

		// Check if a login exists	
		checkForLogin();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void checkConnection(View v) {

		task = CHECK;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Connecting...") {
			@Override
			public void onResponseReceived(String result) {
				((AuthenticationActivity) mContext).handleResponse(result);
			}
		};

		String url = serviceUrl + "/Test";
		wst.execute(new String[] { url });
	}

	public void loginUser(View v) {

		if(validateForm()) {
			task = LOGIN;

			String username = getUsername();
			String password = getPassword();

			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Signing in...") {
				@Override
				public void onResponseReceived(String result) {
					((AuthenticationActivity) mContext).handleResponse(result);
				}
			};

			wst.addParam("username", username);
			wst.addParam("password", password);

			String url = serviceUrl + "/Login";

			wst.execute(new String[] { url });
		}

	}

	public void registerUser(View v) {

		if(validateForm()) {

			task = REGISTER;

			String username = getUsername();
			String password = getPassword();

			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Registering user...") {
				@Override
				public void onResponseReceived(String result) {
					((AuthenticationActivity) mContext).handleResponse(result);
				}
			};

			wst.addParam("username", username);
			wst.addParam("password", password);
			wst.addParam("facebook", "false");

			String url =  serviceUrl + "/Register";

			wst.execute(new String[] { url });
		}

	}
	
	public void loginWithFacebook(String username, String name, String email, String location, String token) {
		task = LOGIN_FB;
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Registering with Facebook...") {
			@Override
			public void onResponseReceived(String response) {
				((AuthenticationActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addParam("username", username);
		wst.addParam("email", email);
		wst.addParam("name", name);
		wst.addParam("location", location);
		wst.addParam("token", token);
		
		String url = serviceUrl + "/LoginFacebook";
		
		wst.execute(new String[] { url });
	}

	public void handleResponse(String response) {

		TextView tvCheck = (TextView) findViewById(R.id.tv_check);
		
		if(response == null || response.length() == 0) {
			tvCheck.setTextColor(Color.RED);
			tvCheck.setText("Connection failed.");
			return;
		}

		try {

			JSONObject jso = new JSONObject(response);

			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");
			
			String authToken;

			switch(task) {
			case CHECK:
				
				tvCheck.setTextColor(Color.GREEN);
				tvCheck.setText(msg);
				break;

			case LOGIN:

				authToken = jso.getString("token");

				if(success) {
					saveAuthToken(authToken);
					Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
					startActivity(intent);
				} else {
					TextView tvMessage = (TextView) findViewById(R.id.message);
					tvMessage.setText(msg);
				}

				break;

			case REGISTER:

				TextView tvMessage = (TextView) findViewById(R.id.message);
				tvMessage.setText(msg);
				
				SimpleAlertDialog dialog = new SimpleAlertDialog(this, "User registered with success!");

				break;

			case AUTHENTICATE:
				if(success) {
					Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
					startActivity(intent);
				} else {
					tvMessage = (TextView) findViewById(R.id.message);
					tvMessage.setText(msg);
				}

				break;
				
			case LOGIN_FB:
				
				authToken = jso.getString("token");
				if(success) {
					saveAuthToken(authToken);
					Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
					startActivity(intent);
				} else {
					tvMessage = (TextView) findViewById(R.id.message);
					tvMessage.setText(msg);
				}
				
				break;

			default:
				Log.e(TAG, "An undefined task just occured.");
				break;
			}

		} catch(Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}
	}

	private String getUsername() {
		EditText editUsername = (EditText) findViewById(R.id.username);
		return editUsername.getText().toString();
	}

	private String getPassword() {
		EditText editPassword = (EditText) findViewById(R.id.password);

		return editPassword.getText().toString();
	}

	private void initSharedPrefs() {
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
	}

	private void saveAuthToken(String token) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("token", token);
		editor.commit();
	}

	private void setAddress() {
		TextView tv = (TextView) findViewById(R.id.tv_address);
		tv.setText(serviceUrl);
	}

	private void setFacebookLogin() {
		callbackManager = CallbackManager.Factory.create();
		LoginButton loginFb = (LoginButton) findViewById(R.id.bn_login_fb);
		loginFb.setReadPermissions(Arrays.asList("public_profile, email, user_location"));

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				GraphRequest.newMeRequest(
						loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject me, GraphResponse response) {
								
								String token = AccessToken.getCurrentAccessToken().getToken();
								
								if (response.getError() != null) {
									// handle error
								} else {
									String username = me.optString("id");
									String email = me.optString("email");
									String name = me.optString("name");

							
									JSONObject locationObj;
									String location = "";
									try {
										locationObj = new JSONObject(me.optString("location"));
										location = locationObj.getString("name");
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									loginWithFacebook(username, email, name, location, token);
								}
							}
						}).executeAsync();
			}

			@Override
			public void onCancel() {
			}

			@Override
			public void onError(FacebookException exception) {
			}


		});

	}

	private void checkForLogin() {
		String token = settings.getString("token", "0");	

		task = AUTHENTICATE;
		if(!token.equals("0")) {
			WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Posting data...") {
				@Override
				public void onResponseReceived(String result) {
					((AuthenticationActivity) mContext).handleResponse(result);
				}
			};
			wst.addHeader("Authorization", token);
			String url =  serviceUrl + "/CheckAuth";

			wst.execute(new String[] { url });
		}
	}

	private boolean validateForm() {

		boolean valid = true;

		EditText etUsername = (EditText) findViewById(R.id.username);
		EditText etPassword = (EditText) findViewById(R.id.password);

		if(etUsername.getText().toString().length() == 0) {
			valid = false;
			etUsername.setError("Please, enter your username.");
		}

		if(etPassword.getText().toString().length() == 0) {
			valid = false;
			etPassword.setError("Please, enter your password.");
		}

		return valid;

	}

	private void settings() {
		Intent intent = new Intent(AuthenticationActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.authentication_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_settings:
			settings();
			break;
		default:
			return super.onOptionsItemSelected(item);

		}

		return true;
	}
}
