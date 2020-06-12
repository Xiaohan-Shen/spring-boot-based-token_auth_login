package service.impl;

import bean.UserInfo;
import dao.UserAccountsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import service.LoginService;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import java.util.Date;

import java.security.Key;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserAccountsDao userAccountsDao;

    private final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String generateToken(String userId) {
        String jws = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + 60000)).signWith(KEY).compact();
        redisTemplate.opsForValue().append(userId, jws);
        return jws;
    }

    @Override
    public boolean isTokenValid(String userId, String token) {
        if (redisTemplate.hasKey(userId)) {
            String jws = (String) redisTemplate.opsForValue().get(userId);
            if (jws.equals(token)) {
                String expirationTime = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(jws).getBody().get("exp").toString();
                if (Integer.parseInt(expirationTime) > System.currentTimeMillis()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
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
