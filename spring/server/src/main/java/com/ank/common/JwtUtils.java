package com.ank.common;

import com.ank.pojo.Administrator;
import com.ank.pojo.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.lang.ref.Cleaner;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    //表示 HTTP 请求头中用于传递 JWT 的字段名 "Authorization"。
    public static final String AUTH_HEADER_KEY = "Authorization";
    //表示 JWT 的前缀 "Bearer "
    public static final String TOKEN_PREFIX = "Bearer ";
    // JWT 签名使用的密钥
    private static final String SECRET = "!34ADAS";

    /**
     * 获取token
     * @param user 信息
     * @return token
     */
    public static String getUserToken(User user) {
        //设置 Token 的过期时间为48小时
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, 48);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("userAccount", user.getAccount())
                .withClaim("userLevel", user.getLevel());

        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SECRET));
    }

    public static String getAdministratorToken(Administrator administrator) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, 8);

        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("administratorAccount", administrator.getAccount())
                .withClaim("creationTime", new Date());

        return builder.withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(SECRET));
    }


    /**
     * 验证 token
     */
    public static DecodedJWT verify(String jwt){
        String token = jwt.substring(JwtUtils.TOKEN_PREFIX.length());
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        return verifier.verify(token);
    }

    public static Map<String, Object> getUserInformation(String jwt){
        DecodedJWT decodedJWT = verify(jwt);
        Claim userAccount = decodedJWT.getClaim("userAccount");
        Claim userLevel = decodedJWT.getClaim("userLevel");
        Map<String, Object> map = new HashMap<>(2);
        //将用户信息和过期时间放入 Map 中
        map.put("userAccount",userAccount.asInt());
        map.put("userLevel",userLevel.asString());
        map.put("overtimeTime", decodedJWT.getExpiresAt());
        return map;
    }
    /**
     * 从 JWT 中提取并返回用户账号
     * */
    public static Integer getUserAccount(String jwt) {
        DecodedJWT decodedJWT = verify(jwt);
        Claim userAccount = decodedJWT.getClaim("userAccount");
        return userAccount.asInt();
    }

    /**
     * 从 JWT 中提取并返回用户等级
     * */
    public static Integer getUserLevel(String jwt) {
        DecodedJWT decodedJWT = verify(jwt);
        Claim userLevel = decodedJWT.getClaim("userLevel");
        return userLevel.asInt();
    }

    public static boolean notAdministrator(String jwt) {
        DecodedJWT decodedJWT = verify(jwt);
        Claim administratorAccount = decodedJWT.getClaim("administratorAccount");
        return administratorAccount.asString() == null;
    }

    public static String getAdministratorAccount(String jwt) {
        DecodedJWT decodedJWT = verify(jwt);
        Claim administratorAccount = decodedJWT.getClaim("administratorAccount");
        return administratorAccount.asString();
    }
}
