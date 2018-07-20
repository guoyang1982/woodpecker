package com.letv.woodpecker.wpwebapp.controller;



import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.entity.UserApp;
import com.letv.woodpecker.wpwebapp.itf.ApplicationService;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import com.letv.woodpecker.wpwebapp.utils.Pagination;
import com.letv.woodpecker.wpwebapp.vo.AppInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author meijunjie
 */
@Slf4j
@Controller
@RequestMapping("/woodpecker/application")
public class ApplicationController extends BaseController {

    @Resource(name = "applicationServiceWeb")
    private ApplicationService applicationService;
    @Resource
    private UserService userService;

    @RequestMapping("/toAppListPage")
    public String queryAllApps(ModelMap model){
        return "application/application";
    }

    @RequestMapping(value = "/queryAppPage",method = RequestMethod.POST)
    public Map<String,Object> queAppPage(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String, String> params = valueMap.toSingleValueMap();
        String userId = params.get("username");
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        Pagination pagination = Pagination.create(pageStart, pageSize);
        List<AppInfo> results = applicationService.queryAllApps(userId,pageStart,pageSize);
        long count = applicationService.queryAppsCount(userId);
        for(int i = 0; i < results.size(); i++)
        {
            AppInfo appInfo = results.get(i);
            if(appInfo != null){
                String ip = appInfo.getIp();
                appInfo.setIp(ip.replaceAll(";","</br>"));
            }
        }
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/queryApp/{appName}",method = {RequestMethod.GET,RequestMethod.POST})
    public ModelAndView queryByAppName(@PathVariable String appName, HttpServletResponse response){
        ModelAndView modelAndView = new ModelAndView();
        try{
            AppInfo appInfo = applicationService.getByAppName(appName);
            modelAndView.addObject("app",appInfo);
            modelAndView.setViewName("application/application_edit");
        }catch (Exception e){
            log.error("query app failed! exception={}",e);
        }
        return modelAndView;
    }
    @RequestMapping("/toAppInfoAddPage")
    public String addAppInfoPage(ModelMap model){
        return "application/application_new";
    }

    @RequestMapping(value = "/deleteApp/{appName}", method = RequestMethod.DELETE)
    public void deleteApp(String userId, @PathVariable("appName") String appName, HttpServletResponse response){
        ResultBean resultBean = new ResultBean(0,"success");
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        userId = authUser.getLoginName();
        try{
            if(authUser.getRole() != RoleIds.SUPER_ADMINER) {
                throw new RuntimeException("请联系管理员删除应用!");
            }
            applicationService.deleteApp(userId,appName);
        }catch (Exception e){
            resultBean.setCode(1);
            resultBean.setMessage(e.getMessage());
            log.error("toAppInfoAddPage fail,{}",e);
        }
        printJSON(response,resultBean);
    }

    @RequestMapping(value = "/saveAppInfo",method = RequestMethod.POST)
    public void saveAppInfo(AppInfo appInfo,HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            appInfo.setCreateTime(new Date());
            appInfo.setModifyTime(new Date());
            appInfo.setStatus(1);
            if(applicationService.getByAppName(appInfo.getAppName()) != null){
                throw new RuntimeException("当前应用已注册，请联系应用所有者加入!");
            }
            applicationService.saveAppInfo(appInfo);
        }catch (Exception e){
            result.setCode(1);
            result.setMessage(e.getMessage());
            log.error("saveAppInfo fail,{}",e);
        }
        printJSON(response,result);
    }

    @RequestMapping(value = "/updateAppInfo", method = RequestMethod.POST)
    public void updateAppInfo(AppInfo appInfo, HttpServletResponse response){
        ResultBean result  = new ResultBean(0,"success");
        try{
            appInfo.set_id(applicationService.getByAppName(appInfo.getAppName()).get_id());
            appInfo.setStatus(1);
            appInfo.setModifyTime(new Date());
            applicationService.updateAppInfo(appInfo);
        }catch (Exception e){
            result.setCode(1);
            result.setMessage("failed!");
        }
        printJSON(response,result);
    }

    @RequestMapping(value = "getIpByAppName/{username}/{appName}",method = RequestMethod.GET)
    public void getIpByAppName(@PathVariable("username") String userId, @PathVariable("appName")String appName, HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            AppInfo appInfo = applicationService.getIpByAppName(userId,appName);
            String[] ip = appInfo.getIp().split(";");
            result.setData(ip);
        }catch (Exception e){
            result.setCode(1);
            result.setMessage("fail!");
            log.error("saveAppInfo fail,{}",e);
        }
        printJSON(response,result);
    }

    @RequestMapping(value="/inviteUser/{appName}")
    public String toApplicationInvite(@PathVariable String appName, ModelMap modelMap){
        AppInfo appInfo = applicationService.getByAppName(appName);
        modelMap.addAttribute("app",appInfo);
        return "application/application_invite";
    }

    @RequestMapping(value = "/saveUserAppInfo", method = RequestMethod.POST)
    public void saveUserAppInfo(AppInfoVo appInfoVo, HttpServletResponse response){
        ResultBean result = new ResultBean(0,"success");
        try{
            // 获取应用ID
            String appId = applicationService.getByAppName(appInfoVo.getAppName()).get_id();
            String[] invitees = appInfoVo.getInvitees().split(";");
            List<Long> userIds = new ArrayList<>(4);
            for(String invitor : invitees){
                User temp = userService.queryByLoginName(invitor);
                if(temp == null){
                    throw  new RuntimeException("当前用户:" + invitor + "未注册, 请重新填写!");
                }else {
                    userIds.add(temp.getId());
                }
            }
            for(Long userId : userIds){
                UserApp temp = new UserApp();
                temp.setAppId(appId);
                temp.setCreateTime(new Date());
                temp.setStatus(1);
                temp.setUserId(userId);
                applicationService.saveUserAppInfos(temp);
            }
        }catch (Exception e){
            result.setCode(1);
            result.setMessage(e.getMessage());
        }
        printJSON(response, result);
    }
}
