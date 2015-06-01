package com.sdis.trafficar.helpers;

public class UserItemAdapter {
	private int id;
	private String username;
	private String location;
	private boolean following;
	
	public UserItemAdapter(int id, String username, String location, boolean following) {
		this.id = id;
		this.username = username;
		this.location = location;
		this.following = following;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getLocation() {
		return location;
	}
	
	public boolean getFollowing() {
		return following;
	}
}
