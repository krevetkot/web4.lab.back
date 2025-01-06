package labs.web4_backend.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.model.Point;
import labs.web4_backend.model.User;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.Validator;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

@Path("/points")
public class PointsResource {
    private final DatabaseManager dbManager;
    private final Validator validator;
    public PointsResource(){
        dbManager = DatabaseManager.getInstance();
        validator = new Validator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoints() {
        JSONObject response = new JSONObject();
        response.put("status", HttpsURLConnection.HTTP_OK);
        response.put("data", dbManager.getPoints());
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPoint(String body) {

        JSONObject request = new JSONObject(body);
        float x = Float.parseFloat(request.optString("x"));
        float y = Float.parseFloat(request.optString("y"));
        float r = Float.parseFloat(request.optString("r"));
        boolean isHit = validator.isHit(x, y, r);
        Point point = new Point(x, y, r, isHit);

        dbManager.insertPoint(point);

        JSONObject response = new JSONObject();
        response.put("status", HttpsURLConnection.HTTP_OK);
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

}
