package labs.web4_backend.resources;

import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.filters.Secured;
import labs.web4_backend.beans.Point;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.JWTUtil;
import labs.web4_backend.utils.AreaValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@Secured
@Path("/points")
public class PointsResource {
    @EJB
    private DatabaseManager dbManager;
    private final AreaValidator areaValidator = new AreaValidator();
    @EJB
    private JWTUtil jwtUtil;
    private static final Logger logger = LogManager.getLogger(PointsResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoints(@HeaderParam("Authorization") String authHeader) {
        logger.info("getPoints");
        JSONObject response = new JSONObject();
        String token = authHeader.substring("Bearer ".length());
        String login = jwtUtil.validateToken(token);
        response.put("data", dbManager.getPoints(login));
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPoint(String body, @HeaderParam("Authorization") String authHeader) {
        logger.info("insertPoint");
        JSONObject request = new JSONObject(body);
        float x = Float.parseFloat(request.optString("x"));
        float y = Float.parseFloat(request.optString("y"));
        float r = Float.parseFloat(request.optString("r"));

        boolean isHit = areaValidator.isHit(x, y, r);

        String token = authHeader.substring("Bearer ".length());
        String login = jwtUtil.validateToken(token);

        Point point = new Point(x, y, r, isHit, login);
        dbManager.insertPoint(point);

        JSONObject response = new JSONObject();
        response.put("isHit", isHit);
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePoints(@HeaderParam("Authorization") String authHeader) {
        logger.info("deletePoints");
        String token = authHeader.substring("Bearer ".length());
        String login = jwtUtil.validateToken(token);
        dbManager.clearAll(login);
        JSONObject response = new JSONObject();
        return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
    }

}
