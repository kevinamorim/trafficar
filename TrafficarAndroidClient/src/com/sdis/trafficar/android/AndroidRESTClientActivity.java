package com.sdis.trafficar.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class AndroidRESTClientActivity extends Activity {
	
	private static final String SERVICE_URL = "http://192.168.1.4:8080/TrafficarAPI/rest/MembershipService/Teste";
	private static final String SERVICE_URL_REGISTER = "http://192.168.1.4:8080/TrafficarAPI/rest/MembershipService/Register";
	private static final String TAG = "AndroidRESTClientActivity";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
	
	public void retrieveSampleData(View v) {
		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "GETting data...");
		wst.execute(new String[] { SERVICE_URL });
	}
	
	public void registerUser(View v) {
		EditText editUsername = (EditText) findViewById(R.id.username);
		EditText editPassword = (EditText) findViewById(R.id.password);
		
		String username = editUsername.getText().toString();
		String password = editPassword.getText().toString();
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Posting data...");
		wst.addNameValuePair("username", username);
		wst.addNameValuePair("password", password);
		
		wst.execute(new String[] { SERVICE_URL_REGISTER });
		
	}
	
	public void handleResponse(String response) {
		 TextView tvMessage = (TextView) findViewById(R.id.message);
		 tvMessage.setText("");
		 
		 try {
			 JSONObject jso = new JSONObject(response);
			 String message = jso.getString("message");
			 boolean test = jso.getBoolean("success");
			 
			 if(test) Log.d(TAG, "Success");
			 else Log.d(TAG, "No success");
			 
			 tvMessage.setText(message);
			 
		 } catch(Exception e) {
			 Log.e(TAG, e.getLocalizedMessage(), e);
		 }
	}
	
	
	
	private class WebServiceTask extends AsyncTask<String, Integer, String> {
		
		public static final int POST_TASK = 1;
		public static final int GET_TASK = 2;
		private static final String TAG = "WebServiceTask";
		
		private static final int CONN_TIMEOUT = 3000;
		private static final int SOCKET_TIMEOUT = 5000;
		
		private int taskType = GET_TASK;
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
            
            Log.d(TAG, result);
 
            return result;
		}
		
		@Override
		protected void onPostExecute(String response) {
			handleResponse(response);
			Log.d(TAG, "chamou");
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
		
	}
	

}
