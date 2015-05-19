package sdis.trafficar.webservices;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.json.MyJSON;

@Path("MembershipService")
public class MembershipService {
	
	@POST
	@Path("/Register")
	@Produces(MediaType.APPLICATION_JSON)
	public String Register(@FormParam("username") String username, @FormParam("password") String password) {
		
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		boolean success = db.registerUser(username, password);
		db.close();
		
		String msg = success ?  "Register successfull." : " Username invalid, try another one.";
		
		return (new MyJSON(success, msg)).toString();
	}
	
	@POST
	@Path("/Login")
	@Produces(MediaType.APPLICATION_JSON)
	public String Login(@FormParam("username") String username, @FormParam("password") String password) {

		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		boolean success = db.loginUser(username, password);
		db.close();
		
		String msg = success ?  "Welcome back!" : " Username or password invalid.";
		
		MyJSON response = new MyJSON(success, msg);
		response.put("username", username);
		
		return response.toString();
	}
	
	@GET
	@Path("/Test")
	@Produces(MediaType.APPLICATION_JSON)
	public String Test() {
		System.out.println("MembershipService/Test");
		return (new MyJSON(true, "Connection established!")).toString();
	}

}
