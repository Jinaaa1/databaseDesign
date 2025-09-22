package com.ank.interceptor;

import com.ank.common.JwtUtils;
import com.ank.exception.JwtException;
import com.ank.exception.BaseException;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String jwt = request.getHeader(JwtUtils.AUTH_HEADER_KEY);
        log.info(jwt);
        // 如果不是映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        // 执行认证
        if (StringUtils.isEmpty(jwt)) {
            throw new JwtException("token为空，请重新登录");
        }
        // KEY加签验证 token
        try {
            DecodedJWT decodedJWT = JwtUtils.verify(jwt);
            log.info("The token will expire at " + decodedJWT.getExpiresAt());
        } catch (SignatureVerificationException e) {
            throw new JwtException("无效签名！");
        } catch (TokenExpiredException e) {
            throw new JwtException("token过期，请重新登录");
        } catch (AlgorithmMismatchException e) {
            throw new JwtException("算法不一致");
        } catch (JWTDecodeException e) {
            throw new JwtException("token格式错误，请重新登录");
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new BaseException(e.getMessage());
        }
        return true;
    }
}
