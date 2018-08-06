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
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * @author meijunjie
 */
@Slf4j
@Controller
@RequestMapping("/woodpecker/application")
public class ApplicationController extends BaseController {

    private static final String WOODPECKER_APPS = "woodpecker_apps";
    private static final String WOODPECKER_APP = "woodpecker_app_";
    @Resource(name = "applicationServiceWeb")
    private ApplicationService applicationService;
    @Resource
    private UserService userService;
    @Autowired
    private Environment env;

    private RedisTemplate redisTemplate;

    /**
     * StringRedisSerializer 用于序列化 字符
     * RedisTemplate默认使用JdkSerializationRedisSerializer 进行序列化，它有个缺点就是生成的序列化文件可读性差，而且占用空间大
     * GenericJackson2JsonRedisSerializer，可以将对象序列化成JSON格式存储在redis中
     *
     * @param redisTemplate redis操作工具
     */
    @Autowired(required = false)
    @SuppressWarnings(value = "unchecked")
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }

    @RequestMapping("/toAppListPage")
    public String queryAllApps(ModelMap model) {
        return "application/application";
    }

    @RequestMapping("/toTermPage")
    public String termPage(String ip, ModelMap model) {
        final String port = env.getProperty("websocket.port", "8080");

        model.addAttribute("ip", ip);
        model.addAttribute("webSocketPort", port);
        return "application/term";
    }

    @RequestMapping(value = "/toAppClustersStatusPage", method = RequestMethod.GET)
    public String queryAllAppClustersStatus() {
        return "application/application_cluster_status";
    }


    @RequestMapping(value = "/queryAllClusterPage", method = RequestMethod.POST)
    @SuppressWarnings(value = "unchecked")
    public Map<String, Object> queryAppClusters(@RequestBody MultiValueMap<String, String> valueMap, HttpServletResponse response) {
        Map<String, String> params = valueMap.toSingleValueMap();
        String userId = params.get("username");
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        Set<String> woodpeckerApps = setOperations.members(WOODPECKER_APPS);

        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        List<AppInfo> results = new ArrayList<>();
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        Pagination pagination = Pagination.create(pageStart, pageSize);
        long count = woodpeckerApps.size();

        // 超级管理员可查询当前所有使用探针的应用
        if (authUser.getRole() == RoleIds.SUPER_ADMINER) {
            for (String woodpeckerApp : woodpeckerApps) {
                AppInfo tempAppInfo = new AppInfo();
                tempAppInfo.setAppName(woodpeckerApp);
                results.add(tempAppInfo);
            }
        } else {
            // 非超级管理员查询其名下的挂载应用
            results = applicationService.queryAllApps(userId, pageStart, pageSize);
            count = applicationService.queryAppsCount(userId);
        }

        // 获取线上正在使用的应用
        for (AppInfo appInfo : results) {
            StringBuilder ip = new StringBuilder("");
            boolean hasAlive = false;
            if (woodpeckerApps.contains(appInfo.getAppName())) {
                // 激活的应用
                String appKey = WOODPECKER_APP + appInfo.getAppName();
                Set<String> allIpAndPort = setOperations.members(appKey);

                int i = 1;
                for (String ipAndPort : allIpAndPort) {
                    String appIpKey = appInfo.getAppName() + "_" + ipAndPort;
                    String appClusterNodeStatus = valueOperations.get(appIpKey);
                    boolean isAlive = appClusterNodeStatus != null && "1".equals(appClusterNodeStatus);
                    if (i++ == 4) {
                        if (isAlive) {
                            // 当前节点存活
                            if (StringUtils.isNotBlank(ipAndPort)) {
                                ip.append("<button type=\"button\" onclick=\"termOpen('" + ipAndPort + "')\"  class=\"btn mini green\">").append(ipAndPort).append("</button>").append("</br></br>");
                                hasAlive = true;
                            }
                        } else {
                            // 当前节点非存活
                            ip.append("<button type=\"button\"  class=\"btn mini gray\" onclick=\" deleteUnusedAppIp(").append("'").append(appInfo.getAppName()).append("'").append(",").append("'").append(ipAndPort.replaceAll("\\.", "-")).append("'").append(")\">").append(ipAndPort).append("</button>").append("</br></br>");

                        }
                    } else {
                        if (isAlive) {
                            // 当前节点存活
                            if (StringUtils.isNotBlank(ipAndPort)) {
                                ip.append("<button type=\"button\" onclick=\"termOpen('" + ipAndPort + "')\"  class=\"btn mini green\">").append(ipAndPort).append("</button>").append("&nbsp;");
                                hasAlive = true;
                            }
                        } else {
                            // 当前节点非存活
                            ip.append("<button type=\"button\"  class=\"btn mini gray\" onclick=\" deleteUnusedAppIp(").append("'").append(appInfo.getAppName()).append("'").append(",").append("'").append(ipAndPort.replaceAll("\\.", "-")).append("'").append(")\">").append(ipAndPort).append("</button>").append("&nbsp;");

                        }
                    }
                }


            } else {
                // don't deal
            }
            if (!hasAlive) {
                StringBuilder temp = new StringBuilder();
                temp.append("<button type=\"button\"  class=\"btn mini gray\" onclick=\" deleteUnusedAppIp(").append("'").append(appInfo.getAppName()).append("'").append(",").append("'").append("all").append("'").append(")\">").append(appInfo.getAppName()).append("</button>").append("</br>");
                appInfo.setAppName(temp.toString());
            }
            appInfo.setIp(ip.toString());
        }

        Map<String, Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/deleteAppNode/{appName}/{ip}", method = RequestMethod.POST)
    @SuppressWarnings(value = "unchecked")
    public void dealUnusedAppIp(@PathVariable String appName, @PathVariable String ip, HttpServletResponse response) {
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        String appKey = "woodpecker_app_" + appName;
        Set<String> allIpAndPort = setOperations.members(appKey);
        String realIp = ip.replaceAll("-", "\\.");
        final String all = "all";
        if (ip.equals(all)) {
            setOperations.remove(WOODPECKER_APPS, appName);
            setOperations.pop(appKey);
        } else {
            for (String temp : allIpAndPort) {
                if (realIp.equals(temp)) {
                    setOperations.remove(appKey, temp);
                    break;
                }
            }
        }

        printJSON(response, new ResultBean(0, "success"));
    }

    @RequestMapping(value = "/queryAppPage", method = RequestMethod.POST)
    public Map<String, Object> queAppPage(@RequestBody MultiValueMap<String, String> valueMap, HttpServletResponse response) {
        Map<String, String> params = valueMap.toSingleValueMap();
        String userId = params.get("username");
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        Pagination pagination = Pagination.create(pageStart, pageSize);
        List<AppInfo> results = applicationService.queryAllApps(userId, pageStart, pageSize);
        long count = applicationService.queryAppsCount(userId);
        for (int i = 0; i < results.size(); i++) {
            AppInfo appInfo = results.get(i);
            if (appInfo != null) {
                String ip = appInfo.getIp();
                appInfo.setIp(ip.replaceAll(";", "</br>"));
            }
        }
        Map<String, Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/queryApp/{appName}", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView queryByAppName(@PathVariable String appName, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            AppInfo appInfo = applicationService.getByAppName(appName);
            modelAndView.addObject("app", appInfo);
            modelAndView.setViewName("application/application_edit");
        } catch (Exception e) {
            log.error("query app failed! exception={}", e);
        }
        return modelAndView;
    }

    @RequestMapping("/toAppInfoAddPage")
    public String addAppInfoPage(ModelMap model) {
        return "application/application_new";
    }

    @RequestMapping(value = "/deleteApp/{appName}", method = RequestMethod.DELETE)
    public void deleteApp(String userId, @PathVariable("appName") String appName, HttpServletResponse response) {
        ResultBean resultBean = new ResultBean(0, "success");
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        userId = authUser.getLoginName();
        try {
            if (authUser.getRole() != RoleIds.SUPER_ADMINER) {
                throw new RuntimeException("请联系管理员删除应用!");
            }
            applicationService.deleteApp(userId, appName);
        } catch (Exception e) {
            resultBean.setCode(1);
            resultBean.setMessage(e.getMessage());
            log.error("toAppInfoAddPage fail,{}", e);
        }
        printJSON(response, resultBean);
    }

    @RequestMapping(value = "/saveAppInfo", method = RequestMethod.POST)
    public void saveAppInfo(AppInfo appInfo, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            appInfo.setCreateTime(new Date());
            appInfo.setModifyTime(new Date());
            appInfo.setStatus(1);
            if (applicationService.getByAppName(appInfo.getAppName()) != null) {
                throw new RuntimeException("当前应用已注册，请联系应用所有者加入!");
            }
            applicationService.saveAppInfo(appInfo);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage(e.getMessage());
            log.error("saveAppInfo fail,{}", e);
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "/updateAppInfo", method = RequestMethod.POST)
    public void updateAppInfo(AppInfo appInfo, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            appInfo.set_id(applicationService.getByAppName(appInfo.getAppName()).get_id());
            appInfo.setStatus(1);
            appInfo.setModifyTime(new Date());
            applicationService.updateAppInfo(appInfo);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("failed!");
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "getIpByAppName/{username}/{appName}", method = RequestMethod.GET)
    public void getIpByAppName(@PathVariable("username") String userId, @PathVariable("appName") String appName, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            AppInfo appInfo = applicationService.getIpByAppName(userId, appName);
            String[] ip = appInfo.getIp().split(";");
            result.setData(ip);
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage("fail!");
            log.error("saveAppInfo fail,{}", e);
        }
        printJSON(response, result);
    }

    @RequestMapping(value = "/inviteUser/{appName}")
    public String toApplicationInvite(@PathVariable String appName, ModelMap modelMap) {
        AppInfo appInfo = applicationService.getByAppName(appName);
        modelMap.addAttribute("app", appInfo);
        return "application/application_invite";
    }

    @RequestMapping(value = "/saveUserAppInfo", method = RequestMethod.POST)
    public void saveUserAppInfo(AppInfoVo appInfoVo, HttpServletResponse response) {
        ResultBean result = new ResultBean(0, "success");
        try {
            // 获取应用ID
            String appId = applicationService.getByAppName(appInfoVo.getAppName()).get_id();
            String[] invitees = appInfoVo.getInvitees().split(";");
            List<Long> userIds = new ArrayList<>(4);
            for (String invitor : invitees) {
                User temp = userService.queryByLoginName(invitor);
                if (temp == null) {
                    throw new RuntimeException("当前用户:" + invitor + "未注册, 请重新填写!");
                } else {
                    userIds.add(temp.getId());
                }
            }
            for (Long userId : userIds) {
                UserApp temp = new UserApp();
                temp.setAppId(appId);
                temp.setCreateTime(new Date());
                temp.setStatus(1);
                temp.setUserId(userId);
                applicationService.saveUserAppInfos(temp);
            }
        } catch (Exception e) {
            result.setCode(1);
            result.setMessage(e.getMessage());
        }
        printJSON(response, result);
    }
}
