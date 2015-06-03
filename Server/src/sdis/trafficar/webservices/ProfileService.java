package sdis.trafficar.webservices;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.AuthToken;
import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.User;
import sdis.trafficar.json.MyJSON;
import sdis.trafficar.utils.AuthenticationUtils;

@Path("ProfileService")
public class ProfileService {
	
	@GET
	@Path("/GetProfile")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetProfile(@HeaderParam("Authorization") String authToken) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authToken)) {
			
			AuthToken token = db.getAuthTokenByToken(authToken);
			
			if(token != null) {
				User user =  token.getUser();
				
				if(user != null) {
					MyJSON response = new MyJSON(true, "Information gathered with success.");
					response.put("username", user.getUsername());
					response.put("email", user.getEmail());
					response.put("name", user.getName());
					response.put("location", user.getLocation());	
					response.put("total_information", "" + user.totalTrafficInformation());
					response.put("feedback", "" + user.totalFeedback());
					
					return response.toString();
				}
			}
			
			return (new MyJSON(false, "Error gathering information.")).toString();

		}

		return AuthenticationUtils.unauthorizedAccess();
	}
	
	@POST
	@Path("/EditProfile")
	@Produces(MediaType.APPLICATION_JSON)
	public String EditProfile(@HeaderParam("Authorization") String authToken, @FormParam("username") String username, 
			@FormParam("email") String email, @FormParam("name") String name, @FormParam("location") String location) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		System.out.println("entrou");
		
		if(AuthenticationUtils.authorize(db, authToken)) {
			AuthToken token = db.getAuthTokenByToken(authToken);
			if(token != null) {
				User user = token.getUser();
				user.setUsername(username);
				user.setEmail(email);
				user.setName(name);
				user.setLocation(location);
				db.editUser(user);
				
				return (new MyJSON(true, "User updated with success.")).toString();
			}
			
			return (new MyJSON(false, "Could not update user.")).toString();
			
		}
		
		return AuthenticationUtils.unauthorizedAccess();
	}

}
