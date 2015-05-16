package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="TrafficInformation")
public class TrafficInformation {
	
	public static final String DESCRIPTION_FIELD_NAME = "description";
	public static final String USER_FIELD_NAME = "user";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField
	private String description;
	
	@DatabaseField(canBeNull=false, foreign=true)
	private User user;
	
	public TrafficInformation() { }

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
}
