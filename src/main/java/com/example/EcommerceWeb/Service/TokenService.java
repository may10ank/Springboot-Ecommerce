package com.example.EcommerceWeb.Service;

import com.example.EcommerceWeb.Repository.BlacklistedTokenRepository;
import com.example.EcommerceWeb.model.BlacklistedToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {

    private final BlacklistedTokenRepository repository;

    public TokenService(BlacklistedTokenRepository repository) {
        this.repository = repository;
    }

    public void blacklistToken(String token, LocalDateTime expiry) {
        BlacklistedToken blacklistedToken = new BlacklistedToken();
        blacklistedToken.setToken(token);
        blacklistedToken.setExpiry(expiry);
        repository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String token) {
        return repository.existsByToken(token);
    }
}
