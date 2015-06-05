package com.sdis.trafficar.android;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sdis.trafficar.android.client.R;
import com.sdis.trafficar.helpers.*;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends ListActivity implements DialogInterface.OnClickListener {

	private String serviceUrl; 
	private static final String TAG = "HomeActivity";

	private static final int UPDATE_TASK = 0;
	private static final int LOGOUT_TASK = 1;
	private static final int THANK_INFO = 2;

	private SharedPreferences settings; 

	private String token;

	private int task = -1;
	
	private int selectedPostId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		settings = this.getSharedPreferences("userdetails", MODE_PRIVATE);
		serviceUrl = ServiceHelpers.getServiceUrl(settings, "/TrafficInformationService");
		token = settings.getString("token", "0");
		
		refresh();
	}

	public void sendTrafficInformation() {
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

		String url = serviceUrl + "/Logout";

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

		String url = serviceUrl + "/GetInfo";

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

				JSONArray postsArray = jso.getJSONArray("info");
				List<TrafficInfoItemAdapter> posts = new ArrayList<TrafficInfoItemAdapter>();
				for(int i = 0; i < postsArray.length(); i++) {
					JSONObject obj = postsArray.getJSONObject(i);
					int id = obj.getInt("id");
					String description = obj.getString("description");
					String location = obj.getString("location");
					String category = obj.getString("category");
					int intensity = obj.getInt("intensity");
					int feedback = obj.getInt("feedback");
					posts.add(new TrafficInfoItemAdapter(id, description, location, category, intensity, feedback));
				}

				updateTrafficInformation(posts);

				break;
			case LOGOUT_TASK:
				
				if(success) {
					Intent intent = new Intent(HomeActivity.this, AuthenticationActivity.class);
					startActivity(intent);
				}
				
				break;
			
			case THANK_INFO: 
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
		case R.id.menu_send:
			sendTrafficInformation();
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

	private void updateTrafficInformation(List<TrafficInfoItemAdapter> posts) {
		TrafficInfoArrayAdapter adapter = new TrafficInfoArrayAdapter(this, posts);
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		TrafficInfoItemAdapter item = (TrafficInfoItemAdapter) getListAdapter().getItem(position);
		selectedPostId = item.getId();
		
		// Build dialog message
		int feedback = item.getFeedback();
		String msg = item.getDescription() + " ( ";
		if(feedback > 0) msg += "+";
		msg += item.getFeedback() + " )";
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setNeutralButton(R.string.cancel, this);
		builder.setPositiveButton(R.string.thanks, this);
		builder.setNegativeButton(R.string.dislike, this);
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch(which) {
		case DialogInterface.BUTTON_POSITIVE:
			giveFeedback(true);
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			giveFeedback(false);
			break;
		default:
			break;
		}
		
		refresh();
	}
	
	private void giveFeedback(boolean positive) {
		
		task = THANK_INFO;

		WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, this, "Thanking for the info..."){
			@Override
			public void onResponseReceived(String response) {
				((HomeActivity) mContext).handleResponse(response);
			}
		};
		
		wst.addHeader("Authorization", token);
		wst.addParam("id", "" + selectedPostId);
		wst.addParam("positive", "" + positive);
		
		selectedPostId = -1;

		String url = serviceUrl + "/Feedback";

		wst.execute(new String[] { url });
	}
}
