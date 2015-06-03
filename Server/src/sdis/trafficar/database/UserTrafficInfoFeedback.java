package sdis.trafficar.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName="UserTrafficInfoFeedback")
public class UserTrafficInfoFeedback {
	
	public static final String TABLE_NAME = "UserTrafficInfoFeedback";
	public static final String ID_FIELD_NAME = "id";
	public static final String USER_FIELD_NAME = "user";
	public static final String INFO_FIELD_NAME = "info";
	public static final String FEEDBACK_FIELD_NAME = "feedback";
	
	@DatabaseField(generatedId=true, allowGeneratedIdInsert=true)
	private int id;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private User user;
	
	@DatabaseField(canBeNull=false, foreign=true, foreignAutoRefresh=true)
	private TrafficInformation info;
	
	@DatabaseField(canBeNull=false)
	private int feedback;
	
	public UserTrafficInfoFeedback() { }
	
	public UserTrafficInfoFeedback(User user, TrafficInformation info, boolean positive) {
		this.user = user;
		this.info = info;
		this.feedback = positive ? 1 : -1;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public TrafficInformation getInfo() {
		return info;
	}
	
	public void setInfo(TrafficInformation info) {
		this.info = info;
	}
	
	public int getFeedback() {
		return feedback;
	}
	
	public void setPositive() {
		feedback = 1;
	}
	
	public void setNegative() {
		feedback = -1;
	}
}
