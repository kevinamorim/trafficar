package com.sdis.trafficar.helpers;

public class UserItemAdapter {
	private int id;
	private String username;
	private String location;
	
	public UserItemAdapter(int id, String username, String location) {
		this.id = id;
		this.username = username;
		this.location = location;
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
}
