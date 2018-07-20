package com.letv.woodpecker.wpwebapp.auth.interceptor;

import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 主要用户获取用户的身份信息 解决嵌套jsp不支持shiro标签的问题
 * @author meijunjie @date 2018/7/9
 */

public class UserInfoInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }

    /**
     * ajax 请求跳转modelAndView
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        if(authUser != null){
            if(modelAndView == null){
                modelAndView = new ModelAndView();
            }
            modelAndView.addObject("username",authUser.getLoginName());
            modelAndView.addObject("role",authUser.getRole());
        }
    }
}
