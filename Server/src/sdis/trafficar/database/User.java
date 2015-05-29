package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
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
	
	public User() {
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
	


}
