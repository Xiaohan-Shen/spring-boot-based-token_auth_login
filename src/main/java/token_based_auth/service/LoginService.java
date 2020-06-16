package token_based_auth.service;

import org.springframework.stereotype.Service;

// TODO：interface不需要注解
@Service
public interface LoginService {
    //TODO：interface 不需要 public
    public String generateToken (String userId);

    public boolean isTokenValid (String token);

    public void deleteToken (String userId);

    public String authenticate (String userId, String password);

    public boolean register (String userId, String password);

}
