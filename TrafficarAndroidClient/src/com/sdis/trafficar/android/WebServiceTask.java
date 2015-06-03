package com.sdis.trafficar.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class WebServiceTask extends AsyncTask<String, Integer, String> implements WebServiceInterface {
	
	public static final int GET_TASK = 1;
	public static final int POST_TASK = 2;
	public static final int DELETE_TASK = 3;

	private static final String TAG = "WebServiceTask";

	private static final int CONN_TIMEOUT = 3000;
	private static final int SOCKET_TIMEOUT = 5000;

	private int taskType = -1;
	protected Context mContext = null;
	private String processMessage = "Processing...";

	private ArrayList<NameValuePair> headers = new ArrayList<NameValuePair>();
	private ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
	
	private ProgressDialog pDlg = null;

	public WebServiceTask(int taskType, Context mContext, String processMessage) {
		this.taskType = taskType;
		this.mContext = mContext;
		this.processMessage = processMessage;
	}
	
	public void addHeader(String name, String value) {
		headers.add(new BasicNameValuePair(name, value));
	}
	
	public void addParam(String name, String value) {
		params.add(new BasicNameValuePair(name, value));
	}

	@Override
	protected void onPreExecute() {
		//hideKeyboard();
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
		onResponseReceived(response);
		pDlg.dismiss();
	}
	
	@Override
	public void onResponseReceived(String result) {
		Log.d(TAG, "Response: " + result);
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
		HttpClient httpclient = new DefaultHttpClient(getHttpParams());

		HttpResponse response = null;

		try {
			switch (taskType) {
			case POST_TASK:
				HttpPost httppost = new HttpPost(url);
				httppost.setEntity(new UrlEncodedFormEntity(params));
				
				// Set headers
				for(int i = 0; i < headers.size(); i++) {
					httppost.addHeader(headers.get(i).getName(), headers.get(i).getValue());
				}
				
				response = httpclient.execute(httppost);
				break;

			case GET_TASK:
				HttpGet httpget = new HttpGet(url);
				
				// Set headers
				for(int i = 0; i < headers.size(); i++) {
					httpget.addHeader(headers.get(i).getName(), headers.get(i).getValue());
				}
				
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
		InputMethodManager inputManager = (InputMethodManager) ((Activity) mContext)
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		inputManager.hideSoftInputFromWindow(((Activity) mContext).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}


}
