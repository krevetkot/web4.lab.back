package labs.web4_backend.utils;

public class Validator {
    public boolean isHit(float x, float y, float r) {
        return ((x >= 0) && (x <= r) && (y >= 0) && (y <= r) || //in rectangle
                (x>=0) && (y >= x/2 - (r/2)) && (y <= 0) || //in triangle
                (x * x + y * y <= r * r ) && (x <= 0) && (y <= 0) //in circle
        );
    }
}
