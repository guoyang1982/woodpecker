package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.auth.shiro.PasswordHash;
import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import com.letv.woodpecker.wpwebapp.utils.Pagination;
import com.letv.woodpecker.wpwebapp.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 *
 * @author meijunjie @date 2018/7/5
 */
@Controller
@Slf4j
@RequestMapping(value = "/woodpecker/user")
public class UserController extends BaseController{
    @Resource
    private UserService userService;

    @Resource
    private PasswordHash passwordHash;


    /**
     * 维护所有用户信息
     * @param model       model
     * @return            用户页
     */
    @RequiresRoles(value = "admin")
    @GetMapping("/toUserListPage")
    public String queryAllUsers(ModelMap model){
        return "user/user";
    }

    @PostMapping(value = "/saveUser")
    public void saveUser(@Validated User user, HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            user.setSalt(UUID.randomUUID().toString());
            user.setPassword(passwordHash.toHex(user.getPassword(),user.getSalt()));
            userService.saveUser(user);
        }catch (Exception e){
            result.setCode(1);
            result.setMessage("failed");
            log.error("save user info error! user={},exception={}",user,e);
        }
            printJSON(response,result);
    }

    /**
     * 按登录名删除用户
     * @param loginName  登录名
     * @param response   响应
     */
    @RequestMapping(value = "/deleteUser/{loginName}",method = {RequestMethod.POST})
    public void deleteUser(@PathVariable String loginName, HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            userService.deleteUser(loginName);
        }catch (Exception e){
            result.setCode(1);
            result.setMessage("failed");
        }
        printJSON(response, result);
    }

    /**
     * 编辑用户信息，如授予用户管理员权限，维护账号状态
     * @param user            用户信息实体
     * @param response        http响应
     */
    @RequiresRoles(value = "admin")
    @PostMapping(value = "/editUser")
    public void editUser(@Validated User user, HttpServletResponse response){
        ResultBean result = new ResultBean(0, "success");
        try{
            User temp = userService.queryByLoginName(user.getLoginName());
            //加密密码密码未变更，只更新用户状态
            if(StringUtils.isBlank(user.getPassword())){
                throw new RuntimeException("密码不能为空!");
            }
            if(user.getPassword().equals(temp.getPassword())){
                temp.setUserRole(user.getUserRole());
                userService.updateUserStatus(temp);
            }else {
                temp.setSalt(UUID.randomUUID().toString());
                temp.setPassword(passwordHash.toHex(user.getPassword(),temp.getSalt()));
                userService.update(temp);
            }

        }catch (Exception e){
            result.setCode(1);
            result.setMessage(e.getMessage());
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "/queryUser/{loginName}",method = {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView queryByLoginName(@PathVariable String loginName, HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView();
        try{
            String[] parameters = loginName.split("_");
            loginName = parameters[0];
            Integer flag = Integer.valueOf(parameters[1]);
            User user = userService.queryByLoginName(loginName);
            modelAndView.addObject("userInfo",user);
            if(flag == 0){
                modelAndView.setViewName("user/user_edit");
            }else {
                modelAndView.setViewName("user/user_change_status");
            }
        }catch (Exception e){
            log.error(e.toString());
        }
        return modelAndView;
    }

    @RequestMapping(value = "/addUserPage",method = {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView addNew(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("user/user_new");
        return modelAndView;
    }

    /**
     * 查询所有用户信息
     * @param valueMap  参数
     * @param response  响应
     * @return          结果列表
     */
    @RequiresRoles(value = "admin")
    @PostMapping(value = "/queryAll")
    public Map<String,Object> queryAll(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        try{
            Map<String, String> params = valueMap.toSingleValueMap();
            Long count = userService.countAll(null,null);
            int page = Integer.valueOf(params.get("iDisplayStart"));
            int pageSize = Integer.valueOf(params.get("iDisplayLength"));
            Pagination pagination = Pagination.create(page, pageSize);
            List<User> users = userService.queryAll(null, null, pagination.getStart(), pagination.getPs());

            users.sort((user1, user2) -> {
                Integer role1 = user1.getUserRole();
                Integer role2 = user2.getUserRole();
                if (role1 < role2) {
                    return -1;
                } else if (role1.equals(role2)) {
                    return 0;
                } else {
                    return 1;
                }

            });
            Map<String,Object> result = getSuccessMap();
            setResContent2Json(response);
            result.put("iTotalRecords", count);
            result.put("iTotalDisplayRecords", count);
            result.put("data", copyBeanInfo(users).toArray());
            return result;
        }catch (Exception e){
           log.error("Query user info error!,exception={}",e);
        }
        return getSuccessMap();
    }

    private List<UserVo> copyBeanInfo(List<User> users){
        List<UserVo> userVos = new ArrayList<>(4);
        for(User user : users){
            UserVo temp = new UserVo();
            BeanUtils.copyProperties(user,temp);
            temp.setRoleName(RoleIds.roleName.get(temp.getUserRole()));
            userVos.add(temp);
        }
        return userVos;
    }
}
