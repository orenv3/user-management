package com.usermanagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * This service
 * generates tokens
 * validates tokens
 * manipulate token - extract email etc
 */
@Service
public class JwtService {

    private static final String SECRET_KEY = //256bit
            "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";


    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(),userDetails);
    }

    public String generateToken(
            Map<String,Object> ngSoftClaims,
            UserDetails userDetails){

       // ngSoftClaims.put("role",((User)userDetails).getRole().name());
        return Jwts.builder()
                .setClaims(ngSoftClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis())) // when the claim created helps to calc the exp
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*48))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); // generates the token
    }

    public <T> T extractClaim(String token, Function<Claims,T> getClaim){
        final Claims claim = extractAllClaims(token);
//        claim.get() Map<>
        return getClaim.apply(claim);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parserBuilder()// in order to parse the token
                .setSigningKey(getSignInKey())// when we encode or generates token we use the signingKey
                                             // signingKey --> the signature in the JWS verify that is not change and the sender is the same
                .build()
                .parseClaimsJws(token)//after the build we can call/parse the token to
                // JWS (JSON Web Signature -> the token object {header,payload,signature} )
                .getBody();//the payload\info\body of the token
    }

    public String extractUserEmail(String token){
        return extractClaim(token,Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserEmail(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() { // get hashed key
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // hmacSha is a type of keyed hash algorithm
    }
}
