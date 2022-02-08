package com.datn.doffice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Component
public class JwtUtils implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60;  // 24 hour

    @Value("${jwt.secret}")
    private String secret;

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private String doGenerateToken(Map<String, Object> claims, MyUserPrincipal myUserPrincipal) {
        claims.put("userId", myUserPrincipal.getUser().getId());
        claims.put("user_name", myUserPrincipal.getUser().getUserName());
        claims.put("email", myUserPrincipal.getUser().getEmail());
        claims.put("roles", myUserPrincipal.getRoles());
        claims.put("permissions", myUserPrincipal.getPermissions());
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(myUserPrincipal.getUsername())
                .setIssuer("Đồ án tốt nghiệp PTIT")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(now.getTime() + JWT_TOKEN_VALIDITY * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    //generate token for user
    public String generateToken(MyUserPrincipal myUserPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, myUserPrincipal);
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
