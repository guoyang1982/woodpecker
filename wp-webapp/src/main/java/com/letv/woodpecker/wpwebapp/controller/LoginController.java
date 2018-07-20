package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.auth.common.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhusheng on 17/3/29.
 * @author zhusheng
 */
@Controller
@Slf4j
public class LoginController extends BaseController{

	/** 验证码服务*/
	@Resource
	private CaptchaService captchaService;

	/**
	 * 登录页
	 * @return  login 登录页
	 */
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	/**
	 * 登录验证接口, 委托shiro进行登录校验,
	 * @param request          HttpServletRequest
	 * @param response         HttpServletResponse
	 * @param userName         用户名
	 * @param password         密码
	 * @param captcha          验证码
	 * @param rememberMe       rememberMe标记
	 */
	@RequestMapping(value = "/woodpecker/login",method = {RequestMethod.GET,RequestMethod.POST})
	public void loginPost(HttpServletRequest request, HttpServletResponse response, String userName, String password, String captcha,
								  @RequestParam(value = "rememberMe",defaultValue = "0") Integer rememberMe){
		ResultBean result = new ResultBean(0,"success");
		try {
			if (StringUtils.isBlank(userName)) {
				throw new RuntimeException("用户名不能为空!");
			}
			if (StringUtils.isBlank(password)) {
				throw new RuntimeException("密码不能为空!");
			}
			if (StringUtils.isBlank(captcha)) {
				throw new RuntimeException("验证码不能为空!");
			}
			if (!captchaService.validate(request, response, captcha)) {
				throw new RuntimeException("验证码错误!");
			}

			Subject user = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName, password);
			token.setRememberMe(1 == rememberMe);

			try{user.login(token);
			}
			catch (Exception e){
				throw new RuntimeException("用户名或密码错误!");
			}
		}catch (Exception e){
			result.setCode(1);
			result.setMessage(e.getMessage());
		}
		printJSON(response, result);
	}

	/**
	 * 用户登出接口，委托shiro进行登出操作
	 * @param request           HttpServletRequest
	 * @param response			HttpServletResponse
	 * @return                  重定向至登录页
	 */
	@RequestMapping(value="/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response){
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return "redirect:/login";
	}


}
