package com.app.Jwt;

import com.app.DTO.request.TokenRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service(value = "jwtRefreshToken")
public class JwtRefreshToken implements JwtProvider {

  @Value("${REFRESH_TOKEN_KEY}")
  private String tokenKey;

  @Value("${refresh.token.expiry.days:30}")
  private long rememberMeExpiryDays;

  @Value("${refresh.token.expiry.default:7}")
  private long defaultExpiryDays;

  private final RedisTemplate<String, Object> redisTemplate;

  @Autowired
  public JwtRefreshToken(RedisTemplate<String, Object> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public String generateToken(TokenRequest request) {
    long time =
        request.isRemember()
            ? TimeUnit.DAYS.toMillis(rememberMeExpiryDays)
            : TimeUnit.DAYS.toMillis(defaultExpiryDays);
    String token = createToken(request.getUsername(), time);
    redisTemplate
        .opsForValue()
        .set("refresh_token:" + request.getUsername(), token, time, TimeUnit.MILLISECONDS);
    return token;
  }

  private String createToken(String username, long time) {
    return Jwts.builder()
        .header()
        .add("alg", "HS512")
        .type("JWT")
        .and()
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + time))
        .signWith(getSignKey())
        .compact();
  }

  private SecretKey getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(tokenKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Override
  public String extractUsernameFromToken(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  @Override
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  @Override
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
  }

  @Override
  public Boolean validateToken(String token, String username) {
    final String usernameInToken = extractUsernameFromToken(token);
    final String storedToken =
        (String) redisTemplate.opsForValue().get("refresh_token:" + username);
    return (username.equals(usernameInToken)) && storedToken != null && storedToken.equals(token);
  }
}
