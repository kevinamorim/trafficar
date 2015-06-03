package com.sdis.trafficar.helpers;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sdis.trafficar.android.client.R;

public class TrafficInfoArrayAdapter extends ArrayAdapter<TrafficInfoItemAdapter> {
	
	private final Context context;
	private List<TrafficInfoItemAdapter> posts;

	public TrafficInfoArrayAdapter(Context context, List<TrafficInfoItemAdapter> items) {
		super(context, R.layout.user_list, items);
		this.context = context;
		
		posts = items;
	}
	
	public void addUser(int id, String description, String location, String category, int intensity, int feedback) {
		TrafficInfoItemAdapter obj = new TrafficInfoItemAdapter(id, description, location, category, intensity, feedback);
		posts.add(obj);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.posts_list, parent, false);
		TextView tvCategory = (TextView) rowView.findViewById(R.id.tvCategory);
		TextView tvLocation = (TextView) rowView.findViewById(R.id.tvLocation);
		tvCategory.setText("[" + posts.get(position).getCategory().charAt(0) + "]");
		tvLocation.setText(posts.get(position).getLocation());
		return rowView;
	}

}
