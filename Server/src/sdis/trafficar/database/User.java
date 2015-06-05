package sdis.trafficar.database;

import java.util.ArrayList;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="User")
public class User {
	
	public static final String ID_FIELD_NAME = "id";
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String PASSWORD_FIELD_NAME = "password";
	public static final String EMAIL_FIELD_NAME = "email";
	public static final String NAME_FIELD_NAME = "name";
	public static final String LOCATION_FIELD_NAME = "location";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField
	private String username;
	
	@DatabaseField
	private String password;
	
	@DatabaseField
	private String email;
	
	@DatabaseField
	private boolean facebookLogin;
	
	@DatabaseField
	private String name;
	
	@DatabaseField
	private String location;
	
	@ForeignCollectionField(eager=true)
	private ForeignCollection<TrafficInformation> trafficInfo;
	
	public User(String username, String password, String email, String name, String location, boolean facebookLogin) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.location = location;
		this.facebookLogin = facebookLogin;
	}
	
	public User() {
		this.username = "";
		this.password = "";
		this.email = "";
		this.name = "";
		this.location = "";
		facebookLogin = false;
	}
	
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean getFacebookLogin() {
		return facebookLogin;
	}
	
	public void setFacebookLogin(boolean facebookLogin) {
		this.facebookLogin = facebookLogin;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int totalTrafficInformation() {
		return trafficInfo.size();
	}
	
	public int totalFeedback() {
		int feedback = 0;
		if(trafficInfo != null) {
			ArrayList<TrafficInformation> tmp = new ArrayList<TrafficInformation>(trafficInfo);
			for(int i = 0; i < tmp.size(); i++) {
				feedback += tmp.get(i).getTotalFeedback();
			}
		}
		return feedback;
	}
	
	@Override
	public String toString() {
		return "{ \"id\":\"" + id + "\", \"username\":\"" + username + "\", \"location\":\"" + location + "\" }";
	}

}
