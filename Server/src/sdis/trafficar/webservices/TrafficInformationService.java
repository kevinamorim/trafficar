package sdis.trafficar.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.TrafficInformation;
import sdis.trafficar.json.MyJSON;

@Path("TrafficInformationService")
public class TrafficInformationService {
	
	@POST
	@Path("/Send")
	@Produces(MediaType.APPLICATION_JSON)
	public String Send(@HeaderParam("Authorization") String authorization, @FormParam("username") String username, @FormParam("description") String description) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(authorize(db, authorization)) {
			db.addTrafficInformation(description, username);
			db.close();
			return (new MyJSON(true, "Register successfull.")).toString();
		}
		
		return unauthorizedAccess();
		
	}
	
	@GET
	@Path("/GetInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetInfo(@HeaderParam("Authorization") String authorization) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(authorize(db, authorization)) {
			List<TrafficInformation> result = db.getTrafficInformation();
			db.close();
			
			MyJSON response = new MyJSON(true, "Information gathered.");
			
			ArrayList<String> values = new ArrayList<String>();
			for(int i = 0; i < result.size(); i++) {
				values.add(result.get(i).getDescription());
			}
			
			response.putArray("info", values);
			return response.toString();
		}
		
		return unauthorizedAccess();

	}
	
	@GET
	@Path("/Logout")
	@Produces(MediaType.APPLICATION_JSON)
	public String Logout(@HeaderParam("Authorization") String authorization) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(authorize(db, authorization)) {
			db.logoutUser(authorization);
			return (new MyJSON(true, "Logout successfull.").toString());
		}
		
		return unauthorizedAccess();
	}
	
	
	private String unauthorizedAccess() {
		return (new MyJSON(false, "Unauthorized access!!!")).toString();
	}
	
	private boolean authorize(MyDatabaseTest db, String authToken) {
		boolean authorized = false;
		
		if(authToken != null) {
			authorized = db.checkAuthToken(authToken);
		}
		
		return authorized;
	}

}
