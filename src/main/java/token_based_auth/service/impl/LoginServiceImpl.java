package token_based_auth.service.impl;

import lombok.extern.slf4j.Slf4j;
import token_based_auth.bean.UserInfo;
import token_based_auth.dao.UserAccountsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import token_based_auth.service.LoginService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import java.util.Date;

import java.security.Key;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserAccountsDao userAccountsDao;

    private final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String generateToken(String userId) {
        String jws = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + 600000)).signWith(KEY).compact();
        if (redisTemplate.hasKey(userId))
            deleteToken(userId);
        redisTemplate.opsForValue().append(userId, jws);
        return jws;
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            String userId = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(token).getBody().get("sub").toString();
            if (redisTemplate.hasKey(userId)) {
                String jws = (String) redisTemplate.opsForValue().get(userId);
                if (jws.equals(token)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return false;
        }
    }

    @Override
    public void deleteToken(String userId) {
        redisTemplate.delete(userId);
    }

    @Override
    public String authenticate (String userId, String password) {
        String userPassword = userAccountsDao.queryByUser(userId).getPassword();
        if ((userAccountsDao.queryByUser(userId)) != null) {
            if (password.equals(userPassword)) {
                return generateToken(userId);
            }
            return null;
        }
        return null;
    }

    @Override
    public boolean register(String userId, String password) {
        if (userAccountsDao.queryByUser(userId) != null)
            return false;
        userAccountsDao.insertUser(new UserInfo(userId, password));
        return true;
    }

}
