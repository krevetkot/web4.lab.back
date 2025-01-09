package labs.web4_backend.utils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import labs.web4_backend.resources.PointsResource;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.util.Date;

@NoArgsConstructor
public class JWTUtil {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_TIME = 3600000; // 1 час
    private static final Logger logger = LogManager.getLogger(PointsResource.class);



    public String generateToken(String username) {
        logger.error(SECRET_KEY);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String validateToken(String token) {
        logger.error(SECRET_KEY);
        return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }
}
