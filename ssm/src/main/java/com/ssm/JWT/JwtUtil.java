package com.ssm.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil  {

    /**
     * token过期时间
     * 密钥
     */
    private static final long TOKEN_VALIDITY = 5*60*60;
    private static final String SECRET = "secret";

    /**
     * 生成token
     * @param claims
     * @return
     */
    public String generateToken(Map<String,Object> claims){
        return Jwts.builder()
                .setClaims(claims)//添加自定义信息
                .setIssuedAt(new Date(System.currentTimeMillis()))//设置当前时间
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000))//设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }
    /**
     * 解析token
     * @param token
     * @return
     */
    public Claims parseToken(String token){
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }
}
