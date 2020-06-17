package token_based_auth.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String generateToken (String userId, int expirationSecond) {
        return Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + expirationSecond * 1000)).signWith(KEY).compact();
    }

    public static String getTokenSubject (String token) {
        return Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody().get("sub").toString();
    }

    public static boolean isTokenValid (String token) {
        try {
            String exp = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody().get("exp").toString();
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        } catch (Exception e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
