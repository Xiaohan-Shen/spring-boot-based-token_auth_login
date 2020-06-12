package service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    public String generateToken (String userId);

    public boolean isTokenValid (String userId);
}
