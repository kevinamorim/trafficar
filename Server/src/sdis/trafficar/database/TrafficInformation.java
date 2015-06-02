package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="TrafficInformation")
public class TrafficInformation {
	
	public static final String TABLE_NAME = "TrafficInformation";
	public static final String ID_FIELD_NAME = "id";
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String LOCATION_FIELD_NAME = "location";
	public static final String INTENSITY_FIELD_NAME = "intensity";
	public static final String USER_FIELD_NAME = "user";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField
	private String description;
	
	@DatabaseField
	private String location;
	
	@DatabaseField
	private int intensity;
	
	@DatabaseField(canBeNull=false)
	private String category;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private User user;
	
	public TrafficInformation() { 
		description = "";
		location = "";
		intensity = 0;
		category = "";
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
	
	public int getIntensity() {
		return intensity;
	}
	
	public void setIntensity(int intensity) {
		this.intensity = intensity;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public String toString() {
		return "{ \"id\":\"" + id + "\", \"description\":\"" + description 
				+ "\", \"location\":\"" + location 
				+ "\", \"category\":\"" + category 
				+ "\", \"intensity\":\"" + intensity 
				+ "\" }";
	}
	
}
