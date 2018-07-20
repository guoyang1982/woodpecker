package com.letv.woodpecker.wpwebapp.auth.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * WebUtils继承自org.springframework.web.util.WebUtils
 * @author meijunjie 2018/7/3
 */
public class WebUtils extends org.springframework.web.util.WebUtils {

    /**
     * 读取cookie值
     * @param request         HttpServletRequest
     * @param cookieName      Cookie名
     * @return                Cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName){
        Cookie cookie = getCookie(request, cookieName);
        return cookie != null ? cookie.getValue() : null;
    }

    /**
     * 失效cookie
     * @param response       HttpServletResponse
     * @param key            Cookie名
     */
    public static void removeCookie(HttpServletResponse response, String key){
        setCookie(response,key,null,0);
    }

    /**
     * 设置cookie
     * @param response        HttpServletResponse
     * @param name            Cookie名
     * @param value           Cookie值
     * @param maxAgeInSeconds Cookie最大存活时间
     */
    public static void setCookie(HttpServletResponse response, String name, String value, int maxAgeInSeconds){
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAgeInSeconds);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
