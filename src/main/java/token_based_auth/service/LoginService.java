package token_based_auth.service;

import org.springframework.stereotype.Service;

import javax.servlet.ServletRequest;
import java.util.Map;

// interface不需要注解
public interface LoginService {
    //interface 不需要 public
    String generateToken (String userId);

    boolean isTokenValid (String token);

    void deleteToken (String userId);

    String authenticate (String userId, String password);

    boolean register (String userId, String password);

    void refreshToken (ServletRequest request, Map<String, Object> map);
}
