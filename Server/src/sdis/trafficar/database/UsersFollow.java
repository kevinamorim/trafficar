package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="UsersFollow")
public class UsersFollow {
	
	public static final String TABLE_NAME = "UsersFollow";
	public static final String ID_FIELD_NAME = "id";
	public static final String SOURCE_USER_FIELD_NAME = "sourceUser";
	public static final String TARGET_USER_FIELD_NAME = "targetUser";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private User sourceUser;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private User targetUser;
	
	public UsersFollow() { }
	
	public User getSource() {
		return sourceUser;
	}
	
	public void setSource(User user) {
		sourceUser = user;
	}
	
	public User getTarget() {
		return targetUser;
	}
	
	public void setTarget(User user) {
		targetUser = user;
	}

}
