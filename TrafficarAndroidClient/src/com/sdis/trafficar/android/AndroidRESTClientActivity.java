package com.sdis.trafficar.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidRESTClientActivity extends Activity {

	private static final String SERVICE_URL = "http://192.168.1.4:8080/TrafficarAPI/rest/MembershipService";
	private static final String TAG = "AndroidRESTClientActivity";

	private static final String HASHING = "SHA-256";
	private static final String CHARSET = "UTF-8";

	private static final int LOGIN = 0;
	private static final int REGISTER = 1;
	private static final int CHECK = 2;

	private int task = -1;

	private boolean success;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}

	public void checkConnection(View v) {

		task = CHECK;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Connecting...");
		String url = SERVICE_URL + "/Test";
		wst.execute(new String[] { url });
	}


	public void loginUser(View v) {

		task = LOGIN;
		success = false;

		String username = getUsername();
		String password = getPassword();

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...");
		wst.addNameValuePair("username", username);
		wst.addNameValuePair("password", password);

		String url = SERVICE_URL + "/Login";

		wst.execute(new String[] { url });

		if(success) {
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra("com.sdis.trafficar.android.USERNAME", username);
			startActivity(intent);
		}

	}

	public void registerUser(View v) {

		task = REGISTER;
		success = false;

		String username = getUsername();
		String password = getPassword();

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...");
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
					Intent intent = new Intent(AndroidRESTClientActivity.this, HomeActivity.class);
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

	private class WebServiceTask extends AsyncTask<String, Integer, String> {

		public static final int GET_TASK = 1;
		public static final int POST_TASK = 2;

		private static final String TAG = "WebServiceTask";

		private static final int CONN_TIMEOUT = 3000;
		private static final int SOCKET_TIMEOUT = 5000;

		private int taskType = -1;
		private Context mContext = null;
		private String processMessage = "Processing...";

		private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		private ProgressDialog pDlg = null;

		public WebServiceTask(int taskType, Context mContext, String processMessage) {
			this.taskType = taskType;
			this.mContext = mContext;
			this.processMessage = processMessage;
		}

		public void addNameValuePair(String name, String value) {
			params.add(new BasicNameValuePair(name, value));
		}

		@Override
		protected void onPreExecute() {
			hideKeyboard();
			showProgressDialog();
		}

		@Override
		protected String doInBackground(String... urls) {
			String url = urls[0];
			String result = "";

			HttpResponse response = doResponse(url);

			if (response == null) {
				return result;
			} else {

				try {

					result = inputStreamToString(response.getEntity().getContent());

				} catch (IllegalStateException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);

				} catch (IOException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}

			}

			return result;
		}

		@Override
		protected void onPostExecute(String response) {
			handleResponse(response);
			pDlg.dismiss();
		}

		private String inputStreamToString(InputStream is) {
			String line = "";
			StringBuilder total = new StringBuilder();

			// Wrap a BufferedReader around the InputStream
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));

			try {
				// Read response until the end
				while ((line = rd.readLine()) != null) {
					total.append(line);
				}
			} catch (IOException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
			}

			// Return full string
			return total.toString();
		}

		private HttpResponse doResponse(String url) {
			// Use our connection and data timeouts as parameters for our
			// DefaultHttpClient
			HttpClient httpclient = new DefaultHttpClient(getHttpParams());

			HttpResponse response = null;

			try {
				switch (taskType) {
				case POST_TASK:
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(params));
					response = httpclient.execute(httppost);
					break;

				case GET_TASK:
					HttpGet httpget = new HttpGet(url);
					response = httpclient.execute(httpget);
					break;

				}
			} catch (Exception e) {

				Log.e(TAG, e.getLocalizedMessage(), e);

			}

			return response;

		}

		private HttpParams getHttpParams() {
			HttpParams htpp = new BasicHttpParams();

			HttpConnectionParams.setConnectionTimeout(htpp, CONN_TIMEOUT);
			HttpConnectionParams.setSoTimeout(htpp, SOCKET_TIMEOUT);

			return htpp;
		}

		private void showProgressDialog() {
			pDlg = new ProgressDialog(mContext);
			pDlg.setMessage(processMessage);
			pDlg.setProgressDrawable(mContext.getWallpaper());
			pDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pDlg.setCancelable(false);
			pDlg.show();
		}

		private void hideKeyboard() {
			InputMethodManager inputManager = (InputMethodManager) AndroidRESTClientActivity.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);

			inputManager.hideSoftInputFromWindow(AndroidRESTClientActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

}
