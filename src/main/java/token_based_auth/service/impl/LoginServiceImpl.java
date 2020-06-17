package token_based_auth.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import token_based_auth.utils.AesUtils;
import token_based_auth.utils.JwtUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    UserAccountsDao userAccountsDao;


    // 我没有真的跑过这个程序啊，但我看起来感觉这个一次性生成的token是不是也是一次性设置的过期时间？
    //  但一般的登录是：Ex.30min无操作后，自动登出。当然，一次性设置为有效使用时间10min也是有需求场景的。
    @Override
    public String generateToken(String userId) {
        // 下面这一行是作为utils static方法随时可调用的。
        String jws = JwtUtils.generateToken(userId, 600);
        redisTemplate.opsForValue().set(userId, jws);
        return jws;
    }

    // TODO：isTokenValid是不是实现的有点复杂了呢？直接把userId对应的token保存起来是不是就足够了？
    // token-based authentication本身token在返回前端时也可以F12查看到token，加密只是为了掩盖包含的信息。
    // 方法本身并没有session-based authentication 安全。好处在于当第二个人登陆同一个账号，会导致第一个人的token失效，
    // 实现被顶掉的操作

    @Override
    public boolean isTokenValid(String token) {
        // 下面这一行是作为utils static方法随时可调用的。
        if (JwtUtils.isTokenValid(token)) {
            String userId = JwtUtils.getTokenSubject(token);
            if (redisTemplate.hasKey(userId)) {
                String jws = (String) redisTemplate.opsForValue().get(userId);
                if (jws.equals(token)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void deleteToken(String userId) {
        redisTemplate.delete(userId);
    }

    @Override
    public String authenticate(String userId, String password) {
        String userPassword = AesUtils.decrypt(userAccountsDao.queryByUser(userId).getPassword());
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
        //  试试看把password加密后存数据库吧，通过某种加密方式。参考bmw utils/AesEncDec
        //  当然，欢迎自己实现个加密算法，哈哈。
        String encryPassword = AesUtils.encrypt(password);
        if (encryPassword != null)
            userAccountsDao.insertUser(new UserInfo(userId, encryPassword));
        return true;
    }

//    @Override
//    public void refreshToken(HttpServletResponse response, String token) {
//        String userId = JwtUtils.getTokenSubject(token);
//        Map<String, Object> map = new HashMap<>();
//        map.put("token", generateToken(userId));
//        JSONObject jsonObj = new JSONObject(map);
//        PrintWriter out = null;
//        try {
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json; charset=utf-8");
//            out = response.getWriter();
//            out.append(jsonObj.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (out != null) {
//                out.close();
//            }
//        }
//    }

    @Override
    public void refreshToken (ServletRequest request, Map<String, Object> map) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("token");
        if (token == null)
            return;
        if (isTokenValid(token)) {
            map.put("refreshed token", generateToken(JwtUtils.getTokenSubject(token)));
        }
    }
}
