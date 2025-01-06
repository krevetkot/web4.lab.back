package labs.web4_backend;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import labs.web4_backend.model.User;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

@Path("/auth")
public class AuthResource {
    private final DatabaseManager dbManager;

    public AuthResource(){
        dbManager = DatabaseManager.getInstance();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(String body) {
        JSONObject request = new JSONObject(body);
        String login = request.optString("login");
        String password = request.optString("password");
        User user = new User(login, password);

        JSONObject response = new JSONObject();
        if (dbManager.userExists(user) && dbManager.checkUserPassword(user)) {
            response.put("status", HttpsURLConnection.HTTP_OK);
            response.put("token", "fake-jwt-token-for-" + login); //надо будет токен потом сделать
            return Response.ok(response.toString(), MediaType.APPLICATION_JSON).build();
        } else {
            response.put("status", HttpsURLConnection.HTTP_UNAUTHORIZED);
            response.put("message", "You are loh");
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(response.toString())
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String register(String login, String password) {
        return "register";
    }
}
