package service;

import org.springframework.stereotype.Service;

@Service
public interface LoginService {
    public String generateToken (String userId);

    public boolean isTokenValid (String userId, String token);

    public void deleteToken (String userId);

    public String authenticate (String userId, String password);

    public boolean register (String userId, String password);
}
