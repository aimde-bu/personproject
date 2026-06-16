package com.ssm.config;

import com.ssm.JWT.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

//拦截器
@Component
public class Interceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("拦截器拦截请求前");
        return true;
//        //3.获取请求头中的token
//        String token = request.getHeader("Authorization");
//
//        //4.判断token是否为空,如果为空,说明未登录,返回错误信息
//        if (token == null || token.isEmpty()){
//            System.out.println("令牌为空");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
//
//        //5.判断token是否正确,如果正确,放行,否则返回错误信息
//        try{
//            jwtUtil.parseToken(token);
//        }catch (Exception e){
//            System.out.println("令牌非法");
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            return false;
//        }
//
//        System.out.println("令牌正确");
//        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("拦截器拦截请求后");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("拦截器拦截请求结束");
    }
}
