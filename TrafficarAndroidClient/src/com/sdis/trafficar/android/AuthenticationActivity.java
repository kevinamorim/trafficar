package com.sdis.trafficar.android;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AuthenticationActivity extends Activity {

	private static final String SERVICE_URL = Constants.BASE_URL + "/MembershipService";
	private static final String TAG = "AuthenticationActivity";

	private static final String HASHING = "SHA-256";
	private static final String CHARSET = "UTF-8";

	private static final int LOGIN = 0;
	private static final int REGISTER = 1;
	private static final int CHECK = 2;

	private int task = -1;

	private CallbackManager callbackManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		setContentView(R.layout.main);
		
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
									String email = me.optString("email");
									String id = me.optString("id");
									Log.e(TAG, "Email: " + email);
									Log.e(TAG, "ID: " + id);
								}
							}
						}).executeAsync();
				TextView tvCheck = (TextView) findViewById(R.id.tv_check);
				tvCheck.setText("LOL");
			}

			@Override
			public void onCancel() {
				Log.e(TAG, "CANCEL");
				TextView tvCheck = (TextView) findViewById(R.id.tv_check);
				tvCheck.setText("LOL");
			}

			@Override
			public void onError(FacebookException exception) {
				Log.e(TAG, "ERROR" + exception);
				TextView tvCheck = (TextView) findViewById(R.id.tv_check);
				tvCheck.setText("LOL");
			}


		});
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

		wst.addNameValuePair("username", username);
		wst.addNameValuePair("password", password);

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

		wst.addNameValuePair("username", username);
		wst.addNameValuePair("password", password);

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

				String username = jso.getString("username");

				if(success) {
					Intent intent = new Intent(AuthenticationActivity.this, HomeActivity.class);
					intent.putExtra("USERNAME", username);
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

		String password = editPassword.getText().toString();

		return hash(password);
	}

	// Credits to: http://stackoverflow.com/questions/5531455/how-to-encode-some-string-with-sha256-in-java
	private String hash(String base) {
		try {
			MessageDigest digest = MessageDigest.getInstance(HASHING);
			byte[] hash = digest.digest(base.getBytes(CHARSET));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

}
