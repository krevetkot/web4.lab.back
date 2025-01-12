package labs.web4_backend.resources;

import io.jsonwebtoken.JwtException;
import jakarta.ejb.EJB;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import labs.web4_backend.beans.Point;
import labs.web4_backend.beans.User;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.JWTUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

@Path("/auth")
public class AuthResource {
    @EJB
    private DatabaseManager dbManager;
    @EJB
    private JWTUtil jwtUtil;
    private static final Logger logger = LogManager.getLogger(AuthResource.class);

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String body) {
        logger.info("login");
        JSONObject request = new JSONObject(body);
        String login = request.optString("login");
        String password = request.optString("password");
        User user = new User(login, password);

        JSONObject response = new JSONObject();
        String accessToken = jwtUtil.generateAccessToken(login);
        String refreshToken = jwtUtil.generateRefreshToken(login);

        if (!dbManager.userExists(user)){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        else if (dbManager.checkUserPassword(user)) {
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } else {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String body) {
        logger.info("register");
        JSONObject request = new JSONObject(body);
        String login = request.optString("login");
        String password = request.optString("password");
        User user = new User(login, password);

        JSONObject response = new JSONObject();
        String accessToken = jwtUtil.generateAccessToken(login);
        String refreshToken = jwtUtil.generateRefreshToken(login);

        if (dbManager.userExists(user)){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        else {
            if (dbManager.addNewUser(user)){
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(response)
                        .build();
            }
        }
    }


    @POST
    @Path("/refresh")
    public Response refresh(@CookieParam("refreshToken") String refreshToken) {
        logger.info("refreshToken");
        JSONObject response = new JSONObject();

        try {
            String username = jwtUtil.validateToken(refreshToken);
            String newToken = jwtUtil.generateAccessToken(username);
            response.put("accessToken", newToken);
            return Response.ok(response.toString(),  MediaType.APPLICATION_JSON).build();
        } catch (JwtException e){
            logger.error(e);
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        }
    }
}
