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

    /**
     * 全局异常配置
     * @return
     */
    @RequestMapping("/toGlobalAlarmConfigPage")
    public String globalAlarmConfigList(){
        return "alarmconfig/globalalarmconfig";
    }

    @RequestMapping("/toAlarmConfigAddPage")
    public String addAlarmConfigPage(String username, String configType, ModelMap model) {
        List<AppInfo> apps = applicationService.queryAllApps(username, 0, Integer.MAX_VALUE);
        model.put("appInfos", apps);
        if(configType.equals("GLOBAL")){
            return "alarmconfig/globalalarmconfig_new";
        }
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
        String configType = params.get("configType");
        long count;
        // 超级管理员可以查看所有告警配置
        if(authUser.getRole() == RoleIds.SUPER_ADMINER){
            results = alarmConfigService.queryAlarmConfigs(userId, configType,pageStart, pageSize);
            count = alarmConfigService.getConfigsCount(userId, configType);

        }else {
            List<String> appNames = new ArrayList<>(4);
            for(UserApp userApp : userAppService.queryList(userIdTemp)){
                appNames.add(applicationService.getByAppId(userApp.getAppId()).getAppName());
            }
            count = alarmConfigService.getConfigsCountByAppNames(appNames,configType);
            List<AppInfo> appInfos = applicationService.queryAllApps(userId, pageStart, pageSize);
            for(AppInfo appInfo : appInfos){
                results.addAll(alarmConfigService.queryListByAppName(appInfo.getAppName(),configType));
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

    @RequestMapping(value = "/deleteConfig/{alarmId}", method = RequestMethod.DELETE)
    public void deleteConfig(@PathVariable("alarmId") String alarmId, HttpServletResponse response) {
        ResultBean resultBean = new ResultBean(0, "success");
        AlarmConfig config = new AlarmConfig();
        config.setAlarmId(alarmId);
        try {
            alarmConfigService.deleteConfig(config);
        } catch (Exception e) {
            resultBean.setCode(1);
            resultBean.setMessage("fail!");
        }
        printJSON(response, resultBean);
    }

    @RequestMapping(value = "/queryById/{id}")
    public ModelAndView queryById(HttpServletResponse response, @PathVariable("id") String id) {
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
        if(alarmConfig.getConfigType() != null && alarmConfig.getConfigType().equals("GLOBAL")){
            modelAndView.setViewName("alarmconfig/globalalarmconfig_edit");
        }else {
            modelAndView.setViewName("alarmconfig/alarmconfig_edit");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/saveAlarmConfig", method = RequestMethod.POST)
    public void saveAlarmConfig(AlarmConfig alarmConfig, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            if(alarmConfig.getMultiple() != null){
                // 检查
                List<AlarmConfig> alarmConfigs = alarmConfigService.queryListByAppName(alarmConfig.getAppName(),"GLOBAL");
                if(alarmConfigs != null && alarmConfigs.size()==1){
                    throw new IllegalArgumentException("应用: " + alarmConfig.getAppName() + " 已存在全局告警配置");
                }
                alarmConfig.setConfigType("GLOBAL");
            }
            alarmConfigService.saveAlarmConfig(alarmConfig);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage(e.getMessage());
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
