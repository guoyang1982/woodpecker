package com.letv.woodpecker.wpwebapp.auth.shiro;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.UserFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author meijunjie @date 2018/7/6
 */
public class ShiroAjaxSessionFilter extends UserFilter{


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if (isLoginRequest(request, response)) {
            return true;
        } else {
            Subject subject = getSubject(request, response);
            // If principal is not null, then the user is known and should be allowed access.
            if(subject.getPrincipal() == null){
                HttpServletRequest req = WebUtils.toHttp(request);
                String xmlHttpRequest = req.getHeader("X-Requested-With");
                if (StringUtils.isNotBlank(xmlHttpRequest)) {
                    if (xmlHttpRequest.equalsIgnoreCase("XMLHttpRequest")) {
                        return true;
                    }
                }
                return false;
            }else {
                return true;
            }
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = WebUtils.toHttp(request);
        String xmlHttpRequest = req.getHeader("X-Requested-With");
        if (StringUtils.isNotBlank(xmlHttpRequest)) {
            if (xmlHttpRequest.equalsIgnoreCase("XMLHttpRequest")) {
                HttpServletResponse res = WebUtils.toHttp(response);
                // 采用res.sendError(401);在Easyui中会处理掉error，$.ajaxSetup中监听不到
                res.setHeader("oauthstatus", "401");
                return false;
            }
        }
        return super.onAccessDenied(request, response);
    }
}
