package com.sdis.trafficar.android;

import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class PostTrafficInfoActivity extends Activity {
	
	private static final String SERVICE_URL = Constants.BASE_URL + "/TrafficInformationService";
	private static final String TAG = "PostTrafficInfoActivity";
	
	private SharedPreferences settings; 
	
	private String token;
	
	private int task = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		createSpinner();
		
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		token = settings.getString("token", "0");
		
	}
	
	public void post(View v) {
		
		EditText etDescription = (EditText) findViewById(R.id.et_description);
		EditText etLocation = (EditText) findViewById(R.id.et_location);
		Spinner spIntensity = (Spinner) findViewById(R.id.sp_intensity);
		
		String description = etDescription.getText().toString();
		String location = etLocation.getText().toString();
		String intensity = spIntensity.getSelectedItem().toString();
		
		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Getting new info...") {
			@Override
			public void onResponseReceived(String response) {
				((PostTrafficInfoActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addHeader("Authorization", token);
		wst.addParam("description", description);
		wst.addParam("location", location);
		wst.addParam("intensity", intensity);
		
		String url = SERVICE_URL + "/Send";
		wst.execute(new String[] { url });
		
	}

	public void handleResponse(String response) {
		
		try {

			JSONObject jso = new JSONObject(response);
			
			Log.d(TAG, response);
			
			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");
			
			if(success) {
				Intent intent = new Intent(PostTrafficInfoActivity.this, HomeActivity.class);
				startActivity(intent);
			} else {
				TextView tvMessage = (TextView) findViewById(R.id.tv_message);
				tvMessage.setText(msg);
			}

			
		} catch(Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

	}
	
	
	private void createSpinner() {
		Spinner spinner = (Spinner) findViewById(R.id.sp_intensity);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.intensity_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(PostTrafficInfoActivity.this, HomeActivity.class);
		finish();
		startActivity(intent);
	}

}
