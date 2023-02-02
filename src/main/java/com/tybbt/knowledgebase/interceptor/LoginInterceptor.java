package com.tybbt.knowledgebase.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    // 用于在业务请求之前校验身份，需对token做校验
    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LOG.info("__________________ Login Interceptor 开始 _________________________");
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);

        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURL().toString();
        LOG.info("接口登录拦截 - path : {}", path);

        String token = request.getHeader("token");
        LOG.info("登录校验开始 - token：{}", token);
        if (token == null || token.isEmpty()) {
            LOG.info("token为空，请求拦截成功");
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false;
        }

        Object object = redisTemplate.opsForValue().get(token);
        if (object == null) {
            LOG.warn("token 无效，请求拦截成功");
            response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401
            return false;
        } else {
            LOG.info("已登录 {}", object);
            return true;
        }

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        LOG.info("------------- LoginInterceptor 结束 耗时：{} ms -------------", System.currentTimeMillis() - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        LOG.info("LogInterceptor 结束");
    }
}
