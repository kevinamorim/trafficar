package com.sdis.trafficar.android;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	
	private SharedPreferences settings;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		settings = this.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		
		setTexts();
	}
	
	private void setTexts() {
		EditText etIpAddress = (EditText) findViewById(R.id.et_ip_config);
		EditText etPort = (EditText) findViewById(R.id.et_port_config);
		
		String ip = settings.getString(Constants.HOST_IP_SETTINGS_NAME, Constants.HOST_IP_DEF_VALUE);
		String port = settings.getString(Constants.HOST_PORT_SETTINGS_NAME, Constants.HOST_PORT_DEF_VALUE);
		
		etIpAddress.setText(ip);
		etPort.setText(port);
	}
	
	public void save(View v) {
			
		SharedPreferences.Editor editor = settings.edit();
		
		EditText etIpAddress = (EditText) findViewById(R.id.et_ip_config);
		EditText etPort = (EditText) findViewById(R.id.et_port_config);
		
		String ipAddress = etIpAddress.getText().toString();
		String port = etPort.getText().toString();
		if(ipAddress.length() > 0) {
			editor.putString(Constants.HOST_IP_SETTINGS_NAME, ipAddress);
		}
		
		if(port.length() > 0) {
			editor.putString(Constants.HOST_PORT_SETTINGS_NAME, port);
		}
		
		editor.commit();
		
		Intent intent = new Intent(SettingsActivity.this, AuthenticationActivity.class);
		startActivity(intent);
		
	}

}
