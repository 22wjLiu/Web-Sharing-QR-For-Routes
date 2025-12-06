package com.liu.qrRoutes.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

  private static final String SECRET = "dhwaudoawhduawdoawhdoahwdohawduhwadohawudhaowdhwoahduwadawodhuad";
  private static final Long EXPIRE_SECONDS = 2 * 60 * 60L; // 2小时

  public static String generateToken(Integer userId, String email, String jti) {
    Date now = new Date();
    Date expire = new Date(now.getTime() + EXPIRE_SECONDS * 1000);

    return Jwts.builder()
      .setSubject(String.valueOf(userId))
      .setId(jti) // jti
      .claim("email", email)
      .setIssuedAt(now)
      .setExpiration(expire)
      .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
      .compact();
  }

  public static Claims parseToken(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
      .build()
      .parseClaimsJws(token)
      .getBody();
  }
}
