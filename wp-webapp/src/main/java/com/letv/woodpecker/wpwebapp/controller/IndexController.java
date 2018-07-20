package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


/**
 * @author leeco
 */
@Controller
public class IndexController extends BaseController {

    /**
     * 直接跳转认证交给shiro完成
     * @return                  啄木鸟管理后台主页
     */
    @RequestMapping(value={"/","/index"})
    public String index(ModelMap modelMap){
        AuthUser user = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        if(user != null){
            modelMap.addAttribute("username",user.getLoginName());
            modelMap.addAttribute("role",user.getRole());
        }
        return "index";
    }

    /**
     * 跳转至注册页面
     * @return                  啄木鸟管理后台注册页
     */
    @GetMapping(value = "/register")
    public String register(){
        return "register";
    }

    @GetMapping(value = "/woodpecker/introduction")
    public String introduction(){
        return "introduction";
    }
}
