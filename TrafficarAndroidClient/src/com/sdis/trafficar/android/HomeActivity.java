package com.sdis.trafficar.android;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		Intent intent = getIntent();
		String username = intent.getStringExtra("USERNAME");
		String text = "You are logged in as: " + username;
		TextView tvUsername = (TextView) findViewById(R.id.tv_username);
		tvUsername.setText(text);
	}
	
	

}
