package sdis.trafficar.webservices;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import sdis.trafficar.database.MyDatabaseTest;
import sdis.trafficar.database.TrafficInformation;

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
	
	@GET
	@Path("/GetInfo")
	@Produces(MediaType.APPLICATION_JSON)
	public String GetInfo() {
		MyDatabaseTest db = new MyDatabaseTest(Constants.DB_NAME);
		List<TrafficInformation> result = db.getTrafficInformation();
		db.close();
		String response = "{ \"success\" : \"true\", \"message\" : \" Register successfull.\"";
		response += ",\"info\" : [ ";
		
		for(int i = 0; i < result.size(); i++) {
			if(i < (result.size() - 1))
				response += "\"" + result.get(i).getDescription() + "\",";
			else
				response += "\"" + result.get(i).getDescription() + "\"";
		}
		
		response += "] }";
		
		return response;
		
	}

}
