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

import sdis.trafficar.database.AuthToken;
import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.User;
import sdis.trafficar.database.UsersFollow;
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

			response.putArray("ids", ids, false);
			response.putArray("usernames", usernames, false);
			response.putArray("locations", locations, false);

			System.out.println(response.toString());

			return response.toString();
		}

		return AuthenticationUtils.unauthorizedAccess();
	}

	@POST
	@Path("/FollowUser")
	@Produces(MediaType.APPLICATION_JSON)
	public String FollowUser(@HeaderParam("Authorization") String authToken, @FormParam("id") int id) {

		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);

		if(AuthenticationUtils.authorize(db, authToken)) {

			AuthToken token = db.getAuthTokenByToken(authToken);
			User source = token.getUser();
			User target = db.getUserById(id);

			if(source != null && target != null) {
				UsersFollow userFollow = new UsersFollow();
				userFollow.setSource(source);
				userFollow.setTarget(target);
				db.addUserFollow(userFollow);

				return (new MyJSON(true, "User followed with success.").toString());
			}

			return (new MyJSON(false, "Could not follow user.").toString());

		}

		return AuthenticationUtils.unauthorizedAccess();
	}

	@GET
	@Path("/Following")
	@Produces(MediaType.APPLICATION_JSON) 
	public String Following(@HeaderParam("Authorization") String authToken) {

		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);

		if(AuthenticationUtils.authorize(db, authToken)) {

			AuthToken token = db.getAuthTokenByToken(authToken);
			User source = token.getUser();

			List<User> users = db.getUsersFollowing(source.getId());
			db.close();

			MyJSON response = new MyJSON(true, "Users obtained with success.");

			ArrayList<String> usersJson = new ArrayList<String>();

			for(int i = 0; i < users.size(); i++) {
				usersJson.add(users.get(i).toString());
			}
			
			response.putArray("users", usersJson, true);

			System.out.println("Response: " + response.toString());
			return response.toString();
		}

		return AuthenticationUtils.unauthorizedAccess();
	}
	
	
	@POST
	@Path("/Unfollow")
	@Produces(MediaType.APPLICATION_JSON)
	public String Unfollow(@HeaderParam("Authorization") String authToken, @FormParam("id") int id) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		if(AuthenticationUtils.authorize(db, authToken)) {
			
			AuthToken token = db.getAuthTokenByToken(authToken);
			User source = token.getUser();
			
			db.unfollowUser(source.getId(), id);
			db.close();
			
			return (new MyJSON(true, "User unfollowed with success.")).toString();
			
		}
		
		return AuthenticationUtils.unauthorizedAccess();
	}
 
}
