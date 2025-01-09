package labs.web4_backend.resources;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.model.Point;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.JWTUtil;
import labs.web4_backend.utils.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@Path("/points")
public class PointsResource {
    private final DatabaseManager dbManager;
    private final Validator validator;
    private final JWTUtil jwtUtil;
    private static final Logger logger = LogManager.getLogger(PointsResource.class);
    public PointsResource(){
        dbManager = DatabaseManager.getInstance();
        validator = new Validator();
        jwtUtil = new JWTUtil();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPoints(@HeaderParam("Authorization") String authHeader) {
        JSONObject response = new JSONObject();
        String token = authHeader.substring("Bearer ".length());
        logger.info(token);
        try {
            jwtUtil.validateToken(token);
            response.put("data", dbManager.getPoints());
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (ExpiredJwtException e){
            response.put("message", "Токен истёк.");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        } catch (JwtException e){
            logger.info(e);
            response.put("message", "Неверный токен.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPoint(String body, @HeaderParam("Authorization") String authHeader) {
        JSONObject request = new JSONObject(body);
        float x = Float.parseFloat(request.optString("x"));
        float y = Float.parseFloat(request.optString("y"));
        float r = Float.parseFloat(request.optString("r"));

        boolean isHit = validator.isHit(x, y, r);
        Point point = new Point(x, y, r, isHit);

        dbManager.insertPoint(point);

        JSONObject response = new JSONObject();
        String token = authHeader.substring("Bearer ".length());
        try {
            jwtUtil.validateToken(token);
            response.put("isHit", isHit);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (ExpiredJwtException e){
            response.put("message", "Токен истёк.");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        } catch (JwtException e){
            response.put("message", "Неверный токен.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
//        response.put("status", HttpsURLConnection.HTTP_OK);
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePoints(@HeaderParam("Authorization") String authHeader) {
        dbManager.clearAll();
        JSONObject response = new JSONObject();

        String token = authHeader.substring("Bearer ".length());
        try {
            jwtUtil.validateToken(token);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } catch (ExpiredJwtException e){
            response.put("message", "Токен истёк.");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        } catch (JwtException e){
            response.put("message", "Неверный токен.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
//        response.put("status", HttpsURLConnection.HTTP_OK);
    }

}
