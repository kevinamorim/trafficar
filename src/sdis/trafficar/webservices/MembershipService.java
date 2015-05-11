package sdis.trafficar.webservices;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabase;

@Path("MembershipService")
public class MembershipService {
	
	@POST
	@Path("/Register")
	@Produces(MediaType.TEXT_HTML)
	public String Register(@FormParam("username") String username, @FormParam("password") String password) {
		MyDatabase db = new MyDatabase("teste");
		db.connect();
		db.init();
		db.registerUser(username, password);
		return "<html><head><meta charset='utf-8'></head><body><h1>Registered user: </h1><h2>" + username + "</h2></body></html>";
	}
	
	@POST
	@Path("/Login")
	@Produces(MediaType.TEXT_HTML)
	public String Login(@FormParam("username") String username, @FormParam("password") String password) {
		MyDatabase db = new MyDatabase("teste");
		db.connect();
		db.init();
		boolean valid = db.checkLogin(username, password);
		
		if(valid) {
			return "<html><head><meta charset='utf-8'></head><body><h1>User logged successfully: </h1><h2>" + username + "</h2></body></html>";
		} else {
			return "<html><head><meta charset='utf-8'></head><body><h1>Username or password invalid</h1></body></html>";
		}
	}

}
