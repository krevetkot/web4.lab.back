package labs.web4_backend.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.filter.Secured;
import labs.web4_backend.model.Point;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@Secured
@Path("/points")
public class PointsResource {
    private final DatabaseManager dbManager;
    private final Validator validator;
    private static final Logger logger = LogManager.getLogger(PointsResource.class);

    public PointsResource() {
        dbManager = DatabaseManager.getInstance();
        validator = new Validator();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoints() {
        logger.info("getPoints");
        JSONObject response = new JSONObject();
        response.put("data", dbManager.getPoints());
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPoint(String body) {
        logger.info("insertPoint");
        JSONObject request = new JSONObject(body);
        float x = Float.parseFloat(request.optString("x"));
        float y = Float.parseFloat(request.optString("y"));
        float r = Float.parseFloat(request.optString("r"));

        boolean isHit = validator.isHit(x, y, r);
        Point point = new Point(x, y, r, isHit);

        dbManager.insertPoint(point);

        JSONObject response = new JSONObject();
        response.put("isHit", isHit);
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePoints() {
        logger.info("deletePoints");
        dbManager.clearAll();
        JSONObject response = new JSONObject();
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

}
