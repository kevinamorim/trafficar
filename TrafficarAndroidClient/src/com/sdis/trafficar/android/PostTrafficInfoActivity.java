package com.sdis.trafficar.android;

import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;
import com.sdis.trafficar.helpers.ServiceHelpers;

import android.app.Activity;
import android.app.AlertDialog;
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
	
	private String serviceUrl; 
	private static final String TAG = "PostTrafficInfoActivity";
	
	private static final int POST_INFO = 0;
	
	private SharedPreferences settings; 
	
	private String token;
	
	private int task = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post);
		createSpinners();
		
		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		serviceUrl = ServiceHelpers.getServiceUrl(settings, "/TrafficInformationService");
		token = settings.getString("token", "0");
		
	}
	
	public void post(View v) {
		
		task = POST_INFO;
		
		EditText etDescription = (EditText) findViewById(R.id.et_description);
		EditText etLocation = (EditText) findViewById(R.id.et_location);
		Spinner spCategory = (Spinner) findViewById(R.id.sp_category);
		Spinner spIntensity = (Spinner) findViewById(R.id.sp_intensity);
		
		String description = etDescription.getText().toString();
		String location = etLocation.getText().toString();
		String category = spCategory.getSelectedItem().toString();
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
		wst.addParam("category", category);
		wst.addParam("intensity", intensity);
		
		String url = serviceUrl + "/Send";
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
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(msg);
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}

			
		} catch(Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

	}
	
	
	private void createSpinners() {
		Spinner spIntensity = (Spinner) findViewById(R.id.sp_intensity);
		ArrayAdapter<CharSequence> adapterIntensity = ArrayAdapter.createFromResource(this, R.array.intensity_array, android.R.layout.simple_spinner_item);
		adapterIntensity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spIntensity.setAdapter(adapterIntensity);
		
		Spinner spCategory = (Spinner) findViewById(R.id.sp_category);
		ArrayAdapter<CharSequence> adapterCategory = ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item);
		adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spCategory.setAdapter(adapterCategory);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(PostTrafficInfoActivity.this, HomeActivity.class);
		finish();
		startActivity(intent);
	}

}
