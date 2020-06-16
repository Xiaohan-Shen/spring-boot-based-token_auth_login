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

    // TODO： 我没有真的跑过这个程序啊，但我看起来感觉这个一次性生成的token是不是也是一次性设置的过期时间？
    //  但一般的登录是：Ex.30min无操作后，自动登出。当然，一次性设置为有效使用时间10min也是有需求场景的。
    @Override
    public String generateToken(String userId) {
        // TODO: 下面这一行是作为utils static方法随时可调用的。
        String jws = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + 600000)).signWith(KEY).compact();
        if (redisTemplate.hasKey(userId))
            deleteToken(userId);
        // TODO：为什么用append？
        redisTemplate.opsForValue().append(userId, jws);
        return jws;
    }

    // TODO：isTokenValid是不是实现的有点复杂了呢？直接把userId对应的token保存起来是不是就足够了？
    @Override
    public boolean isTokenValid(String token) {
        try {
            // TODO: 下面这一行是作为utils static方法随时可调用的。
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
        // TODO: 试试看把password加密后存数据库吧，通过某种加密方式。参考bmw utils/AesEncDec
        //  当然，欢迎自己实现个加密算法，哈哈。
        userAccountsDao.insertUser(new UserInfo(userId, password));
        return true;
    }

}
