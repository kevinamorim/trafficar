package sdis.trafficar.utils;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.json.MyJSON;

public class AuthenticationUtils {
	
	public static String unauthorizedAccess() {
		return (new MyJSON(false, "Unauthorized access!!!")).toString();
	}
	
	public static boolean authorize(MyDatabaseTest db, String authToken) {
		boolean authorized = false;
		
		if(authToken != null) {
			authorized = db.checkAuthToken(authToken);
		}
		
		return authorized;
	}

}
