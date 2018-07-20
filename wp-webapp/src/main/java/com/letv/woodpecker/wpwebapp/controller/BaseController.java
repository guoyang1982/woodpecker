package com.letv.woodpecker.wpwebapp.controller;

import com.alibaba.fastjson.JSON;
import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import lombok.Data;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhusheng on 17/3/27.
 */
public class BaseController
{

    Map<String, Object> getSuccessMap()
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "0");
        result.put("msg", "成功");
        return result;
    }

    protected Map<String, Object> getFailMap(String rsMsg)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", "A00001");
        result.put("msg", rsMsg);
        return result;
    }

    void printJSON(HttpServletResponse servletResponse, Object obj)
    {
        PrintWriter writer = null;
        try {
            servletResponse.setContentType("application/json");
            writer = servletResponse.getWriter();
            writer.print(JSON.toJSONString(obj));
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }
    void setResContent2Json(HttpServletResponse response) {
        response.setContentType("application/json; charset=UTF-8");
        response.setHeader("Cache-Control", "no-store");
    }

    String getUseId(HttpServletRequest request){
       Subject subject = SecurityUtils.getSubject();
        if(subject!=null){
            AuthUser authUser = (AuthUser) subject.getPrincipal();
            return authUser.getLoginName();
        }
        return "";
    }

    @Data
    class ResultBean{
        private int code;
        private String message;
        private Object data;
        ResultBean(int code, String message){
            this.code = code;
            this.message = message;
        }
    }


}
