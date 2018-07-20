package com.letv.woodpecker.wpwebapp.controller;


import com.letv.woodpecker.wpdatamodel.model.AlarmConfig;
import com.letv.woodpecker.wpdatamodel.model.RuleConfig;
import com.letv.woodpecker.wpdatamodel.service.AlarmConfigService;
import com.letv.woodpecker.wpdatamodel.service.RuleConfigService;
import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import com.letv.woodpecker.wpwebapp.entity.UserApp;
import com.letv.woodpecker.wpwebapp.itf.ApplicationService;
import com.letv.woodpecker.wpwebapp.itf.UserAppService;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhusheng on 17/3/29.
 */
@Slf4j
@Controller
@RequestMapping("/woodpecker/alarmconfig")
public class AlarmConfigController extends BaseController {
    @Resource
    private AlarmConfigService alarmConfigService;
    @Resource(name = "applicationServiceWeb")
    private ApplicationService applicationService;
    @Resource
    private RuleConfigService ruleConfigService;
    @Resource
    private UserService userService;
    @Resource
    private UserAppService userAppService;


    @RequestMapping("/toAlarmConfigPage")
    public String alarmConfigList(ModelMap model) {
        return "alarmconfig/alarmconfig";
    }

    @RequestMapping("/toAlarmConfigAddPage")
    public String addAlarmConfigPage(String username, ModelMap model) {
        List<AppInfo> apps = applicationService.queryAllApps(username, 0, Integer.MAX_VALUE);
        model.put("appInfos", apps);
        return "alarmconfig/alarmconfig_new";
    }

    @RequestMapping(value = "/queryAlarmConfigPage", method = RequestMethod.POST)
    public Map<String, Object> queAppPage(@RequestBody MultiValueMap<String, String> valueMap, HttpServletResponse response) {
        Map<String, String> params = valueMap.toSingleValueMap();
        String userId = params.get("username");
        Long userIdTemp = userService.queryByLoginName(userId).getId();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        List<AlarmConfig> results = new ArrayList<>(4);

        long count;
        // 超级管理员可以查看所有告警配置
        if(authUser.getRole() == RoleIds.SUPER_ADMINER){
            results = alarmConfigService.queryAlarmConfigs(userId, pageStart, pageSize);
            count = alarmConfigService.getConfigsCount(userId);
        }else {
            List<String> appNames = new ArrayList<>(4);
            for(UserApp userApp : userAppService.queryList(userIdTemp)){
                appNames.add(applicationService.getByAppId(userApp.getAppId()).getAppName());
            }
            count = alarmConfigService.getConfigsCountByAppNames(appNames);
            List<AppInfo> appInfos = applicationService.queryAllApps(userId, pageStart, pageSize);
            for(AppInfo appInfo : appInfos){
                results.addAll(alarmConfigService.queryListByAppName(appInfo.getAppName()));
            }
        }
        for (int i = 0; i < results.size(); i++) {
            AlarmConfig alarmConfig = results.get(i);
            alarmConfig.setAlarmId(alarmConfig.get_id());
            String email = alarmConfig.getEmail();
            if(!StringUtils.isEmpty(email)){
                alarmConfig.setEmail(email.replaceAll(";", "</br>"));
            }
            String phone = alarmConfig.getPhoneNum();
            if(!StringUtils.isEmpty(phone)){
                alarmConfig.setPhoneNum(phone.replaceAll(";", "</br>"));
            }
        }
        Map<String, Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/deleteConfig/{userId}/{appName}/{ip}/{exceptionType}", method = RequestMethod.DELETE)
    public void deleteConfig(@PathVariable("userId") String userId, @PathVariable("appName") String appName,
                             @PathVariable("ip") String ip, @PathVariable("exceptionType") String exceptionType, HttpServletResponse response) {
        ResultBean resultBean = new ResultBean(0, "success");
        AlarmConfig config = new AlarmConfig();
        config.setUserId(userId);
        config.setAppName(appName);
        config.setIp(ip);
        config.setExceptionType(exceptionType);
        try {
            alarmConfigService.deleteConfig(config);
        } catch (Exception e) {
            resultBean.setCode(1);
            resultBean.setMessage("fail!");
        }
        printJSON(response, resultBean);
    }

    @RequestMapping(value = "/queryById/{id}")
    public ModelAndView queryById(HttpServletResponse reponse, @PathVariable("id") String id) {
        AlarmConfig alarmConfig = alarmConfigService.queryAlarmConfig(id);

        List<RuleConfig> ruleConfigs = ruleConfigService.queryRuleConfigs(alarmConfig.getAppName());
        for (int i = 0; i < ruleConfigs.size(); i++) {
            RuleConfig ruleConfig = ruleConfigs.get(i);
            ruleConfig.setRuleId(ruleConfig.get_id());
        }
        alarmConfig.setAlarmId(alarmConfig.get_id());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("alarmConfig", alarmConfig);
        modelAndView.addObject("ruleConfigs", ruleConfigs);
        modelAndView.setViewName("alarmconfig/alarmconfig_edit");
        return modelAndView;
    }

    @RequestMapping(value = "/saveAlarmConfig", method = RequestMethod.POST)
    public void saveAlarmConfig(AlarmConfig alarmConfig, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            alarmConfigService.saveAlarmConfig(alarmConfig);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "/modifyAlarmConfig", method = RequestMethod.POST)
    public void modifyAlarmConfig(AlarmConfig alarmConfig, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            alarmConfig.set_id(alarmConfig.getAlarmId());
            alarmConfigService.modifyAlarmConfig(alarmConfig);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
        }
        printJSON(response, result);
    }
}
