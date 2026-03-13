package com.journalApp.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;
    private SecretKey getSigningkey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token){
        Claims claims=extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getSigningkey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }



    public String generateToken(String username){
        Map<String,Object>claims=new HashMap<>();
        return createToken(claims,username);
    }


    public String createToken(Map<String,Object>claims,String subject) {
        return Jwts.builder().claims(claims).subject(subject).header().empty().add("typ","JWT")
                .and().issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*15))// 15 min time for expiry of jwt token
                .signWith(getSigningkey()).compact();
    }

    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token){
        return !isTokenExpired(token);
    }

}
