package com.mockAi.MOCAI.Services;

import com.mockAi.MOCAI.Entites.AppUser;
import com.mockAi.MOCAI.Security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String sk;

    private SecretKey getKey() {
        byte[] bytes = Base64.getDecoder().decode(sk.getBytes());
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(CustomUserDetails userDetails) {
        return Jwts.builder()
            .claim("userId", userDetails.getUserId())
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 5 * 60))
            .signWith(getKey())
            .compact();
    }


    public String generateToken(AppUser user) {
        return Jwts.builder()
            .claim("userId", user.getUserId())
            .setSubject(user.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 5 * 60))
            .signWith(getKey())
            .compact();
    }


    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <R> R extractClaim(String token, Function<Claims, R> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    public boolean isValid(String token, CustomUserDetails userDetails) {
        String user = extractSubject(token);
        return user.equals(userDetails.getUsername()) && isExpired(token);
    }

    public boolean isExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().after(new Date()); // if the expiration of the token is before the currentDate then it is expired
    }

}
