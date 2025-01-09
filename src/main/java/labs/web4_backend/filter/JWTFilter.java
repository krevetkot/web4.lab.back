package labs.web4_backend.filter;

import jakarta.annotation.Priority;
import labs.web4_backend.utils.DatabaseManager;
import labs.web4_backend.utils.JWTUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTFilter implements ContainerRequestFilter {
    private static final Logger logger = LogManager.getLogger(JWTFilter.class);
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString("Authorization");
        logger.error(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            JWTUtil jwtUtil = new JWTUtil();
            String username = jwtUtil.validateToken(token);
            requestContext.setProperty("username", username);
        } catch (RuntimeException e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
