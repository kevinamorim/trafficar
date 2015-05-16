package sdis.trafficar.webservices;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;

@Path("TrafficInformationService")
public class TrafficInformationService {
	
	@POST
	@Path("/Send")
	@Produces(MediaType.APPLICATION_JSON)
	public String Send(@FormParam("username") String username, @FormParam("description") String description) {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		db.addTrafficInformation(description, username);
		db.close();
		return "{ \"success\" : \"true\", \"message\" : \" Register successfull.\" }";
	}

}
