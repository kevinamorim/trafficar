package com.sdis.trafficar.helpers;

public class TrafficInfoItemAdapter {
	
	private int id;
	private String description;
	private String location;
	private String category;
	private int intensity;
	private int feedback;
	
	public TrafficInfoItemAdapter(int id, String description, String location, String category, int intensity, int feedback) {
		this.id = id;
		this.description = description;
		this.location = location;
		this.category = category;
		this.intensity = intensity;
		this.feedback = feedback;
	}
	
	public int getId(){
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public int getIntensity() {
		return intensity;
	}
	
	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}
	
	public int getFeedback() {
		return feedback;
	}
	
	public void setFeedback(int feedback) {
		this.feedback = feedback;
	}

}
