package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="AuthToken")
public class AuthToken {
	
	public static final String TOKEN_FIELD_NAME = "token";
	public static final String USER_FIELD_NAME = "user";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField
	private String token;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private User user;
	
	public AuthToken() { }
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	

}
