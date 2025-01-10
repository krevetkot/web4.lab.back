package labs.web4_backend.utils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.SecretKey;
import java.util.Date;

public class JWTUtil {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long ACCESS_EXPIRATION_TIME = 3600000; // 1 час
    private final long REFRESH_EXPIRATION_TIME = ACCESS_EXPIRATION_TIME*24*30; //30 дней
    private static final Logger logger = LogManager.getLogger(JWTUtil.class);

    public JWTUtil(){

    }

    public String generateAccessToken(String username) {
        logger.info("generateAccessToken");
        logger.info(SECRET_KEY);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String generateRefreshToken(String username) {
        logger.info("generateRefreshToken");
        logger.info(SECRET_KEY);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String validateToken(String token) {
        logger.info("validateToken");
        logger.info(SECRET_KEY);
        return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }
}
