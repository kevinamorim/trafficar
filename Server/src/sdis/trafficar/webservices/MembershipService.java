package sdis.trafficar.webservices;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabase;

@Path("MembershipService")
public class MembershipService {
	
	@POST
	@Path("/Register")
	@Produces(MediaType.APPLICATION_JSON)
	public String Register(@FormParam("username") String username, @FormParam("password") String password) {
		MyDatabase db = new MyDatabase("teste");
		db.connect();
		db.init();
		boolean success = db.registerUser(username, password);
		db.close();
		
		if(success) {
			return "{ \"success\" : \"true\", \"message\" : \" Register successfull.\" }";
		} else {
			return "{ \"success\" : \"false\", \"message\" : \" Username invalid, try another one.\" }";
		}

	}
	
	@POST
	@Path("/Login")
	@Produces(MediaType.TEXT_HTML)
	public String Login(@FormParam("username") String username, @FormParam("password") String password) {
		MyDatabase db = new MyDatabase("teste");
		db.connect();
		db.init();
		boolean valid = db.checkLogin(username, password);
		db.close();
		
		if(valid) {
			return "{ \"success\" : \"true\", \"message\" : \" Welcome back! \", \"username\" : \"" + username + "\"}";
		} else {
			return "{ \"success\" : \"false\", \"message\" : \" Username or password invalid.\" }";
		}
	}
	
	@GET
	@Path("/Test")
	@Produces(MediaType.APPLICATION_JSON)
	public String Test() {
		return "{ \"success\" :  \"true\", \"message\" : \" Connection established! \" }";
	}

}
