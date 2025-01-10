package labs.web4_backend;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import labs.web4_backend.filter.JWTFilter;
import labs.web4_backend.resources.AuthResource;
import labs.web4_backend.resources.PointsResource;

import java.util.HashSet;
import java.util.Set;


@ApplicationPath("/api")
public class BackendApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(JWTFilter.class);
        classes.add(AuthResource.class);
        classes.add(PointsResource.class);
        return classes;
    }
}