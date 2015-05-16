package com.sdis.trafficar.android;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
	
}
