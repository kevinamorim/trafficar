package sdis.trafficar.webservices;
import javax.security.auth.login.LoginException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.User;
import sdis.trafficar.json.MyJSON;
import sdis.trafficar.utils.AuthenticationUtils;

@Path("MembershipService")
public class MembershipService {
	
	@POST
	@Path("/Register")
	@Produces(MediaType.APPLICATION_JSON)
	public String Register(@FormParam("username") String username, @FormParam("password") String password) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		boolean success = db.registerUser(username, AuthenticationUtils.hash(password), "", "", "", false);
		
		db.close();
		
		String msg = success ?  "Register successfull." : " Username invalid, try another one.";
		
		return (new MyJSON(success, msg)).toString();
	}
	
	@POST
	@Path("/Login")
	@Produces(MediaType.APPLICATION_JSON)
	public String Login(@FormParam("username") String username, @FormParam("password") String password) {
		String msg = "";
		String token = "";
		boolean success = false;
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		
		try {
			
			token = db.loginUser(username, AuthenticationUtils.hash(password));
			msg = "Welcome back!";
			success = true;
			
		} catch(LoginException e) {
			
			msg = e.getMessage();
			
		}
	
		db.close();
		
		MyJSON response = new MyJSON(success, msg);
		response.put("token", token);
		
		return response.toString();
	}
	
	@GET
	@Path("/CheckAuth")
	@Produces(MediaType.APPLICATION_JSON)
	public String CheckAuth(@HeaderParam("Authorization") String authorization) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		boolean success = AuthenticationUtils.authorize(db, authorization);
		db.close();
		
		String msg = (success) ? "Authentication valid." : "Authentication failed.";
		
		return (new MyJSON(success, msg).toString());
	}

	
	@POST
	@Path("/LoginFacebook")
	@Produces(MediaType.APPLICATION_JSON)
	public String LoginFacebook(@FormParam("username") String username, @FormParam("email") String email, 
			@FormParam("name") String name, @FormParam("location") String location, @FormParam("token") String token) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		boolean success = false;
		User user = db.checkIfUserExists(username);
		if(user == null) {
			// Register new user
			db.registerUser(username, AuthenticationUtils.generateRandomPassword(), email, name, location, true);
			user = db.checkIfUserExists(username);
		}
		
		if(user != null) {
			db.addAuthToken(token, user);
			success = true;
		}
		
		String msg = (success) ? "Authentication valid." : "Authentication failed.";
		MyJSON response = new MyJSON(success, msg);
		
		if(success) response.put("token", token);
		
		return (response.toString());
		
	}
	
	@GET
	@Path("/Test")
	@Produces(MediaType.APPLICATION_JSON)
	public String Test() {
		System.out.println("MembershipService/Test");
		return (new MyJSON(true, "Connection established!")).toString();
	}

}
