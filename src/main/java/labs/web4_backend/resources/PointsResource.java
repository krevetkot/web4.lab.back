package labs.web4_backend.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.model.User;
import labs.web4_backend.utils.DatabaseManager;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

@Path("/points")
public class PointsResource {
    private final DatabaseManager dbManager;
    public PointsResource(){
        dbManager = DatabaseManager.getInstance();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoints() {
        JSONObject response = new JSONObject();
        response.put("status", HttpsURLConnection.HTTP_OK);
        response.put("data", dbManager.getPoints());
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }
}
