package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="User")
public class User {
	
	public static final String USERNAME_FIELD_NAME = "username";
	public static final String PASSWORD_FIELD_NAME = "password";
	
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
	
	public User() {
		
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
	
	


}
