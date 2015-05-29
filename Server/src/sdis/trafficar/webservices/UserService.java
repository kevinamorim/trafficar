package sdis.trafficar.webservices;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.User;
import sdis.trafficar.json.MyJSON;
import sdis.trafficar.utils.AuthenticationUtils;

@Path("UserService")
public class UserService {
	
	@GET
	@Path("/GetUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetUsers(@HeaderParam("Authorization") String authToken) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authToken)) {
			List<User> users = db.getAllUsers();
			db.close();
			
			MyJSON response = new MyJSON(true, "Users obtained with success.");
			
			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<String> usernames = new ArrayList<String>();
			ArrayList<String> locations = new ArrayList<String>();
			
			for(int i = 0; i < users.size(); i++) {
				ids.add("" + users.get(i).getId());
				usernames.add(users.get(i).getUsername());
				locations.add(users.get(i).getLocation());
			}
			
			response.putArray("ids", ids);
			response.putArray("usernames", usernames);
			response.putArray("locations", locations);
			
			System.out.println(response.toString());
			
			return response.toString();
		}
		
		return AuthenticationUtils.unauthorizedAccess();
	}

}
