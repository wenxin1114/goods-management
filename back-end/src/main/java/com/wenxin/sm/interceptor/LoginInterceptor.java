package com.wenxin.sm.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wenxin.sm.entity.Result;
import com.wenxin.sm.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    //在方法前调用
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String Authorization = request.getHeader("Authorization");
        if (Authorization != null && Authorization.startsWith("Bearer ")) {
            String token = Authorization.substring(7);
            boolean verify = TokenUtils.verify(token);
            if (!verify) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8");
                // Jackson 序列化
                ObjectMapper objectMapper = new ObjectMapper();
                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(Result.NoLogin()));
                writer.close();
            }
            request.setAttribute("token",token);
            return verify;
        }
        return true;
    }
}

