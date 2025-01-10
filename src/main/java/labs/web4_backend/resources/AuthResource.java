package labs.web4_backend.resources;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.filter.Secured;
import labs.web4_backend.model.User;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.JWTUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

@Path("/auth")
public class AuthResource {
    private final DatabaseManager dbManager;
    private final JWTUtil jwtUtil;
    private static final Logger logger = LogManager.getLogger(AuthResource.class);

    public AuthResource(){
        dbManager = DatabaseManager.getInstance();
        jwtUtil = new JWTUtil();
    }

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
        String token = jwtUtil.generateToken(login);

        if (!dbManager.userExists(user)){
            response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
            response.put("message", "No user with such login. Please, sign up.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        else if (dbManager.checkUserPassword(user)) {
            response.put("status", HttpsURLConnection.HTTP_OK);
            response.put("token", token);
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } else {
            response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
            response.put("message", "Wrong password.");
            return Response.status(Response.Status.UNAUTHORIZED)
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
        String token = jwtUtil.generateToken(login);

        if (dbManager.userExists(user)){
            response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
            response.put("message", "User with such login already exists. Please, sign in.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        else {
            if (dbManager.addNewUser(user)){
                response.put("status", HttpsURLConnection.HTTP_OK);
                response.put("token", token);
                return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
            } else {
                response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
                response.put("message", "Registration is failed.");
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(response)
                        .build();
            }
        }
    }


    @Secured
    @POST
    @Path("/refresh")
    public Response refresh(@HeaderParam("Authorization") String authHeader) {
        logger.info("refreshToken");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        JSONObject response = new JSONObject();
        String token = authHeader.substring("Bearer ".length());
        String username = null;
        try {
            username = jwtUtil.validateToken(token);
            String newToken = jwtUtil.generateToken(username);
            response.put("token", newToken);
            return Response.ok(response.toString(),  MediaType.APPLICATION_JSON).build();
        } catch (ExpiredJwtException e){
            logger.error(e);
            String newToken = jwtUtil.generateToken(username);
            response.put("token", newToken);
            return Response.ok(response.toString(),  MediaType.APPLICATION_JSON).build();
        } catch (JwtException e){
            logger.error(e);
            response.put("message", "Неверный токен.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
    }
}
