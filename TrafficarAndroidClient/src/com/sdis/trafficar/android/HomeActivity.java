package com.sdis.trafficar.android;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends Activity {

	private static final String SERVICE_URL = Constants.BASE_URL + "/TrafficInformationService";
	private static final String TAG = "HomeActivity";

	private static final int UPDATE_TASK = 0;
	private static final int LOGOUT_TASK = 1;

	private SharedPreferences settings; 

	private String token;

	private int task = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		token = settings.getString("token", "0");
	}

	public void sendTrafficInformation(View v) {
		Intent intent = new Intent(HomeActivity.this, PostTrafficInfoActivity.class);
		startActivity(intent);	
	}

	public void logout() {
		
		task = LOGOUT_TASK;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting new info..."){
			@Override
			public void onResponseReceived(String result) {
				((HomeActivity) mContext).handleResponse(result);
			}
		};

		wst.addHeader("Authorization", token);

		String url = SERVICE_URL + "/Logout";

		wst.execute(new String[] { url });

	}

	public void refresh() {
		
		task = UPDATE_TASK;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.GET_TASK, this, "Getting new info..."){
			@Override
			public void onResponseReceived(String response) {
				((HomeActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addHeader("Authorization", token);

		String url = SERVICE_URL + "/GetInfo";

		wst.execute(new String[] { url });

	}

	public void profile() {
		Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
		startActivity(intent);
	}
	
	public void explore() {
		Intent intent = new Intent(HomeActivity.this, ExploreActivity.class);
		startActivity(intent);
	}

	public void handleResponse(String response) {

		try {

			JSONObject jso = new JSONObject(response);

			Log.d(TAG, response);

			boolean success = jso.getBoolean("success");
			String msg = jso.getString("message");

			switch(task) {
			case UPDATE_TASK:

				JSONArray params = jso.getJSONArray("info");
				ArrayList<String> information = new ArrayList<String>();

				for(int i = 0; i < params.length(); i++) {
					information.add(params.getString(i));
				}		

				updateTrafficInformation(information);

				break;
			case LOGOUT_TASK:
				
				if(success) {
					Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
					startActivity(intent);
				}
				
				break;
			default:
				break;
			}



		} catch(Exception e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.home_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_refresh:
			refresh();
			break;
		case R.id.menu_search:
			explore();
			break;
		case R.id.menu_account:
			profile();
			break;
		case R.id.menu_logout:
			logout();
			break;
		default:
			return super.onOptionsItemSelected(item);
			
		}

		return true;
	}

	private void updateTrafficInformation(ArrayList<String> information) {
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tl_information);
		tableLayout.removeAllViews();

		for(int i = 0; i < information.size(); i++) {
			TableRow row = new TableRow(this);
			TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(lp);
			TextView tv = new TextView(this);
			tv.setText(information.get(i));
			row.addView(tv);
			tableLayout.addView(row);
		}
	}

}
