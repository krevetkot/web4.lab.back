package labs.web4_backend.utils;

public class Validator {
    public boolean isHit(float x, float y, float r) {
        return ((x >= -r/2) && (x <= 0) && (y >= -r) && (y <= 0) || //in rectangle
                (x >= 0) && (y <= 0) && (y >= x - (r/2)) || //in triangle
                (x * x + y * y <= (r/2) * (r/2) ) && (x >= 0) && (y >= 0) //in circle
        );
    }
}
