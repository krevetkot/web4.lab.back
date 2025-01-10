package labs.web4_backend.resources;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
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
            response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
            response.put("message", "User with such login already exists. Please, sign in.");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response)
                    .build();
        }
        else {
            if (dbManager.addNewUser(user)){
                response.put("status", HttpsURLConnection.HTTP_OK);
                response.put("accessToken", accessToken);
                response.put("refreshToken", refreshToken);
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
            response.put("message", "Неверный refresh токен.");
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(response)
                    .build();
        }
    }
}
