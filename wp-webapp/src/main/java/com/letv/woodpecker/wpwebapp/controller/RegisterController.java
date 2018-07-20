package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.auth.common.CaptchaService;
import com.letv.woodpecker.wpwebapp.auth.shiro.PasswordHash;
import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 *
 * @author meijunjie @date 2018/7/11
 */
@Controller
@Slf4j
public class RegisterController extends BaseController {

    @Resource
    private CaptchaService captchaService;
    @Resource
    private UserService userService;
    @Resource
    private PasswordHash passwordHash;

    @RequestMapping(value = "/woodpecker/register", method = {RequestMethod.GET, RequestMethod.POST})
    public void registerUser(String userName,String password,String captcha, HttpServletRequest request,HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            if(StringUtils.isBlank(captcha)){
                throw new RuntimeException("验证码不能为空");
            }
            if(!captchaService.validate(request,response,captcha)){
                throw new RuntimeException("验证码错误");
            }
            if(userService.queryByLoginName(userName)!=null){
                throw new RuntimeException("用户名已注册");
            }
            User user = new User();
            user.setLoginName(userName);
            user.setUserName(userName);
            user.setPassword(password);
            user.setSalt(UUID.randomUUID().toString());
            // 加密存储用户密码
            user.setPassword(passwordHash.toHex(user.getPassword(),user.getSalt()));
            // 首次注册的用户为普通用户，相关权限需管理员分配
            user.setUserRole(RoleIds.NORMAL_USER);
            userService.saveUser(user);
        }catch (RuntimeException e){
            result.setCode(1);
            result.setMessage(e.getMessage());
        }
        printJSON(response, result);
    }
}
