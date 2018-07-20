package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.auth.common.CaptchaService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证码生成接口，登录时下发验证码
 * @author meijunjie 2018/7/3
 */
@Controller
public class CaptchaController extends BaseController {

    @Resource
    private CaptchaService captchaService;

    @GetMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response){
        captchaService.generateCaptchaCode(request,response);
    }
}
