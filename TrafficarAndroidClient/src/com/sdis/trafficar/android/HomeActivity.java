package com.sdis.trafficar.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends Activity {
	
	private static final String SERVICE_URL = Constants.BASE_URL + "/TrafficInformationService";
	private static final String TAG = "HomeActivity";
	
	private String username = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		Intent intent = getIntent();
		username = intent.getStringExtra("USERNAME");
		String text = "You are logged in as: " + username;
		TextView tvUsername = (TextView) findViewById(R.id.tv_username);
		tvUsername.setText(text);
	}
	
	public void sendTrafficInformation(View v) {
		
		EditText etDescription = (EditText) findViewById(R.id.et_description);
		String description = etDescription.getText().toString();
	
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Sending traffic info...");
		wst.addNameValuePair("username", username);
		wst.addNameValuePair("description", description);
		
		String url = SERVICE_URL + "/Send";
		wst.execute(new String[] { url });
		
	}
	
	public void refresh(View v) {
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting new info..."){
			@Override
			public void onResponseReceived(String result) {
				((HomeActivity) mContext).handleResponse(result);
			}
		};
		
		String url = SERVICE_URL + "/GetInfo";
		
		wst.execute(new String[] { url });
		
		
	}
	
	public void handleResponse(String response) {
		
		try {

			JSONObject jso = new JSONObject(response);
			
			Log.d(TAG, response);
			
			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");
			
			JSONArray params = jso.getJSONArray("info");
			ArrayList<String> information = new ArrayList<String>();
			
			for(int i = 0; i < params.length(); i++) {
				information.add(params.getString(i));
			}
			
			Log.d(TAG, "" + information.size());
			Log.d(TAG, information.get(0));
			
			TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout1);
			
			for(int i = 0; i < information.size(); i++) {
				TableRow row = new TableRow(this);
				TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
				row.setLayoutParams(lp);
				TextView tv = new TextView(this);
				tv.setText(information.get(i));
				row.addView(tv);
				tableLayout.addView(row);
			}
			
		} catch(Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

	}
	
}
