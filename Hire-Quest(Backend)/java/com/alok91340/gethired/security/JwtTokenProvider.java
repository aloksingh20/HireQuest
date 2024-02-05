package com.alok91340.gethired.security;

import java.security.SignatureException;
import java.util.Date;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.alok91340.gethired.exception.GetHiredException;


@Component
public class JwtTokenProvider {

    @Value("${app.jwt-secret}")
    private String jwtSecret;
    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationTime;

    // generate token method

    public String generateToken(UserDetails userDetails) {
        String userName = userDetails.getUsername();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationTime);

        String token = Jwts.builder()
                .setSubject(userName)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return token;
    }

    // get username from the token, retrieve username from generated token
    public String getUserNameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // validate JWT token
    public boolean validateToken(String token) throws SignatureException {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            throw new GetHiredException("Invalid JWT token", HttpStatus.BAD_REQUEST);
        } catch (ExpiredJwtException e) {
            throw new GetHiredException("Expired JWT token", HttpStatus.BAD_REQUEST);
        } catch (UnsupportedJwtException e) {
            throw new GetHiredException("Unsupported JWT token", HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException e) {
            throw new GetHiredException("JWT claims string is empty", HttpStatus.BAD_REQUEST);
        }
    }
}
