package com.letv.woodpecker.wpwebapp.controller;

import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.letv.woodpecker.wpdatamodel.model.RuleConfig;
import com.letv.woodpecker.wpdatamodel.service.RuleConfigService;
import com.letv.woodpecker.wpwebapp.utils.GroovyTool;
import lombok.extern.slf4j.Slf4j;
import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import com.letv.woodpecker.wpwebapp.constants.RoleIds;
import com.letv.woodpecker.wpwebapp.entity.UserApp;
import com.letv.woodpecker.wpwebapp.itf.ApplicationService;
import com.letv.woodpecker.wpwebapp.itf.UserAppService;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午1:24
 */
@Slf4j
@Controller
@RequestMapping("/woodpecker/ruleConfig")
public class RuleConfigController extends BaseController {

    @Resource(name = "applicationServiceWeb")
    private ApplicationService applicationService;
    @Resource
    private RuleConfigService ruleConfigService;
    @Resource
    private UserService userService;
    @Resource
    private UserAppService userAppService;


    @RequestMapping("/ruleConfigPage")
    public String alarmConfigList(ModelMap model) {
        return "ruleconfig/ruleconfig";
    }

    @RequestMapping("/toRuleConfigAddPage")
    public String addRuleConfigPage(String username, ModelMap model) {
        List<AppInfo> apps = applicationService.queryAllApps(username, 0, Integer.MAX_VALUE);
        model.put("appInfos", apps);
        String rule = "def validate(String exceptionInfo) {\n" +
                "\n" +
                "    return false;\n" +
                "}";
        model.put("rule", rule);
        return "ruleconfig/ruleconfig_new";
    }


    @RequestMapping("/ruleConfigTest")
    public String ruleConfigTest(String ruleConfig, String exceptionInfo, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            Object resObject = GroovyTool.getInstance().runGroovyScript(ruleConfig, "validate", new String[]{exceptionInfo});
            if (null == resObject) {
                result.setCode(1);
                result.setMessage("fail!");
            } else {
                result.setData(resObject);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
        }
        printJSON(response, result);
        return "ruleconfig/ruleconfig_new";
    }

    @RequestMapping(value = "/queryRuleConfigPage", method = RequestMethod.POST)
    public Map<String, Object> queryAppPage(@RequestBody MultiValueMap<String, String> valueMap, HttpServletResponse response) {
        Map<String, String> params = valueMap.toSingleValueMap();
        String userId = params.get("username");
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        List<RuleConfig> results = new ArrayList<>(4);
        Long count;
        Long userIdTemp = userService.queryByLoginName(userId).getId();
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        // 超级管理员可以查看所有的告警规则
        if(authUser.getRole() == RoleIds.SUPER_ADMINER){
             results = ruleConfigService.queryRuleConfigs(userId, pageStart, pageSize);
             count = ruleConfigService.getConfigsCount(userId);

        }else {
            List<String> appNames = new ArrayList<>(4);
            for(UserApp userApp : userAppService.queryList(userIdTemp)){
                appNames.add(applicationService.getByAppId(userApp.getAppId()).getAppName());
            }
            count = ruleConfigService.getConfigsCountByAppName(appNames);
            List<AppInfo> appInfos = applicationService.queryAllApps(userId, pageStart, pageSize);
            for(AppInfo appInfo : appInfos){
                results.addAll(ruleConfigService.queryRuleConfigs(appInfo.getAppName()));
            }
        }

        for (int i = 0; i < results.size(); i++) {
            RuleConfig ruleConfig = results.get(i);
            ruleConfig.setRuleId(ruleConfig.get_id());
        }

        Map<String, Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/deleteConfig/{id}", method = RequestMethod.DELETE)
    public void deleteConfig(@PathVariable("id") String id, HttpServletResponse response) {
        ResultBean resultBean = new ResultBean(0, "success");
        RuleConfig config = new RuleConfig();
        config.set_id(id);

        try {
            ruleConfigService.deleteConfig(config);
        } catch (Exception e) {
            resultBean.setCode(1);
            resultBean.setMessage("fail!");
        }
        printJSON(response, resultBean);
    }

    @RequestMapping(value = "/queryById/{id}/{username}")
    public ModelAndView queryById(HttpServletResponse reponse, @PathVariable("id") String id, @PathVariable("username") String username) {
        RuleConfig ruleConfig = ruleConfigService.queryRuleConfig(id);
        ruleConfig.setRuleId(ruleConfig.get_id());
        List<AppInfo> apps = applicationService.queryAllApps(username, 0, Integer.MAX_VALUE);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("ruleConfig", ruleConfig);
        modelAndView.addObject("appInfos", apps);

        modelAndView.setViewName("ruleconfig/ruleconfig_edit");
        return modelAndView;
    }

    @RequestMapping(value = "/saveRuleConfig", method = RequestMethod.POST)
    public void saveRuleConfig(RuleConfig ruleConfig, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            ruleConfig.setCTime(timeForNow());
            ruleConfig.setMTime(timeForNow());
            ruleConfigService.saveRuleConfig(ruleConfig);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "/modifyRuleConfig", method = RequestMethod.POST)
    public void modifyRuleConfig(RuleConfig ruleConfig, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            ruleConfig.set_id(ruleConfig.getRuleId());
            ruleConfig.setMTime(timeForNow());
            ruleConfigService.modifyRuleConfig(ruleConfig);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "getRuleByAppName/{appName}", method = RequestMethod.GET)
    public void getRuleByAppName(@PathVariable("appName") String appName, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            if (appName.equals("0")) {
                result.setCode(1);
                result.setMessage("fail!");
            }else {
                List<RuleConfig> ruleConfigs = ruleConfigService.queryRuleConfigs(appName);
                for (int i = 0; i < ruleConfigs.size(); i++) {
                    RuleConfig ruleConfig = ruleConfigs.get(i);
                    ruleConfig.setRuleId(ruleConfig.get_id());
                }
                result.setData(ruleConfigs);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
            log.error("get Info fail,{}", e);
        }
        printJSON(response, result);
    }

    private String timeForNow() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return format.format(now);
    }
}
