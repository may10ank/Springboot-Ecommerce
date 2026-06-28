package com.example.EcommerceWeb.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private String secretKey;
    private static final String SECRET_KEY = "502c72b4c46eb049af37670af2f6b70b49b02079d4386db66b18ff747bb87b46";

    public String generateToken(String email,String role){
        return Jwts.builder().
                setSubject(email)
                .claim("role",role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*10))
                .signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }


    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token){
      return extractClaim(token,Claims::getSubject);
    }

    public String extractRole(String token){
        return (String) Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody().get("role");
    }

    public boolean validateToken(String token,String email){
        return email.equals(extractUserName(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }
}
