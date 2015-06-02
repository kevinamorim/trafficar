package com.sdis.trafficar.helpers;

import java.util.List;

import com.sdis.trafficar.android.client.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UserArrayAdapter extends ArrayAdapter<UserItemAdapter> {
	
	private final Context context;
	private List<UserItemAdapter> users;

	public UserArrayAdapter(Context context, List<UserItemAdapter> items) {
		super(context, R.layout.user_list, items);
		this.context = context;
		
		users = items;
	}
	
	public void addUser(int id, String username, String location) {
		UserItemAdapter obj = new UserItemAdapter(id, username, location, false);
		users.add(obj);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.user_list, parent, false);
		TextView tvUsername = (TextView) rowView.findViewById(R.id.tvUsername);
		TextView tvLocation = (TextView) rowView.findViewById(R.id.tvLocation);
		tvUsername.setText(users.get(position).getUsername());
		tvLocation.setText("(" + users.get(position).getLocation() + ")");
		return rowView;
	}

}


