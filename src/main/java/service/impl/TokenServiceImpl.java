package service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import service.TokenService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import java.util.Date;

import java.security.Key;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public String generateToken(String userId) {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + 60000)).signWith(key).compact();
        redisTemplate.opsForValue().append(userId, jws);
        return jws;
    }

    @Override
    public boolean isTokenValid(String userId) {
        return false;
    }
}
