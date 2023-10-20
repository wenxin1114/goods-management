package com.wenxin.sm.utils;


import com.wenxin.sm.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


public class TokenUtils {


    //过期时间七天
    private static final long EXPIRE_TIME = TimeUnit.DAYS.toMillis(7);
    //密钥
    private static final String TOKEN_SECRET = "wenxin666";


    //生成token

    /**
     * 生成token
     *
     * @param user 用户数据
     * @return token字符串
     */
    public static String createToken(User user) {


        // token过期时间 7天

        HashMap<String, Object> claims = new HashMap<>();

        claims.put("username", user.getUsername());
        claims.put("userid", user.getId());
        claims.put("role", user.getRole());

        return Jwts.builder().
                setClaims(claims)//写入数据 用户信息
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) //设置过期时间
                .signWith(SignatureAlgorithm.HS512, TOKEN_SECRET) //设置加密形式和指定字符
                .compact();


    }

    /**
     * 解析Token
     *
     * @param token 需要校验的token
     * @return 校验是否成功
     */
    public static Claims parse(String token) {
        try {

            return Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token).getBody();

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 校验token
     *
     * @param token 需要校验的token
     * @return 校验是否成功
     */
    public static boolean verify(String token) {
        Claims claims = parse(token);
        return claims != null;
    }


}






