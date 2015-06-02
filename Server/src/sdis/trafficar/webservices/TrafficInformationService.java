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
import sdis.trafficar.utils.AuthenticationUtils;

@Path("TrafficInformationService")
public class TrafficInformationService {
	
	@POST
	@Path("/Send")
	@Produces(MediaType.APPLICATION_JSON)
	public String Send(@HeaderParam("Authorization") String authorization, 
			@FormParam("description") String description, @FormParam("location") String location, @FormParam("category") String category, @FormParam("intensity") String intensity) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authorization)) {
			int intensityInt = Integer.parseInt(intensity);
			db.addTrafficInformation(authorization, description, location, category, intensityInt);
			db.close();
			return (new MyJSON(true, "Traffic information posted with success.")).toString();
		}
		
		return AuthenticationUtils.unauthorizedAccess();
		
	}
	
	@GET
	@Path("/GetInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetInfo(@HeaderParam("Authorization") String authorization) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authorization)) {
			List<TrafficInformation> result = db.getTrafficInformation();
			db.close();
			
			MyJSON response = new MyJSON(true, "Information gathered.");
			
			ArrayList<String> postsJson = new ArrayList<String>();
			for(int i = 0; i < result.size(); i++) {
				postsJson.add(result.get(i).toString());
			}
			
			response.putArray("info", postsJson, true);
			
			System.out.println("Response: " + response.toString());
			return response.toString();
		}
		
		return AuthenticationUtils.unauthorizedAccess();

	}
	
	@GET
	@Path("/Logout")
	@Produces(MediaType.APPLICATION_JSON)
	public String Logout(@HeaderParam("Authorization") String authorization) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authorization)) {
			db.logoutUser(authorization);
			return (new MyJSON(true, "Logout successfull.").toString());
		}
		
		return AuthenticationUtils.unauthorizedAccess();
	}
	
	


}
