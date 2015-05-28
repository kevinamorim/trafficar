package com.sdis.trafficar.android;

import java.util.Arrays;

import org.json.JSONObject;

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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AuthenticationActivity extends Activity {
	
	private SharedPreferences settings;

	private static final String SERVICE_URL = Constants.BASE_URL + "/MembershipService";
	private static final String TAG = "AuthenticationActivity";

	private static final int LOGIN = 0;
	private static final int REGISTER = 1;
	private static final int CHECK = 2;
	private static final int AUTHENTICATE = 3;

	private int task = -1;

	private CallbackManager callbackManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.main);

		setAddress();
		setFacebookLogin();
		
		// Check if a login exists
		initSharedPrefs();
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

		String url = SERVICE_URL + "/Test";
		wst.execute(new String[] { url });
	}

	public void loginUser(View v) {

		task = LOGIN;

		String username = getUsername();
		String password = getPassword();

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...") {
			@Override
			public void onResponseReceived(String result) {
				((AuthenticationActivity) mContext).handleResponse(result);
			}
		};

		wst.addParam("username", username);
		wst.addParam("password", password);

		String url = SERVICE_URL + "/Login";

		wst.execute(new String[] { url });

	}

	public void registerUser(View v) {

		task = REGISTER;

		String username = getUsername();
		String password = getPassword();

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...") {
			@Override
			public void onResponseReceived(String result) {
				((AuthenticationActivity) mContext).handleResponse(result);
			}
		};

		wst.addParam("username", username);
		wst.addParam("password", password);

		String url =  SERVICE_URL + "/Register";

		wst.execute(new String[] { url });

	}

	public void handleResponse(String response) {

		try {

			JSONObject jso = new JSONObject(response);

			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");

			switch(task) {
			case CHECK:

				TextView tvCheck = (TextView) findViewById(R.id.tv_check);
				tvCheck.setText(msg);
				break;

			case LOGIN:

				String authToken = jso.getString("token");

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

				break;
				
			case AUTHENTICATE:
				Log.d(TAG, "ENTROU!!!!!");
				if(success) {
					Log.d(TAG, "SUCESSO!!!!!");
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
		tv.setText(Constants.BASE_URL);
	}
	
	private void setFacebookLogin() {
		callbackManager = CallbackManager.Factory.create();
		LoginButton loginFb = (LoginButton) findViewById(R.id.bn_login_fb);
		loginFb.setReadPermissions(Arrays.asList("public_profile, email"));
		
		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				GraphRequest.newMeRequest(
						loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject me, GraphResponse response) {
								if (response.getError() != null) {
									// handle error
								} else {
									String username = me.optString("username");
									String email = me.optString("email");
									String id = me.optString("id");
									Log.e(TAG, "User: " + username);
									Log.e(TAG, "Email: " + email);
									Log.e(TAG, "ID: " + id);
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
			String url =  SERVICE_URL + "/CheckAuth";

			wst.execute(new String[] { url });
		}
	}
}
