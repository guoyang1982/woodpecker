package com.letv.woodpecker.wpwebapp.controller;



import com.letv.woodpecker.wpdatamodel.model.ExceptionInfo;
import com.letv.woodpecker.wpdatamodel.service.ExceptionService;
import com.letv.woodpecker.wpwebapp.auth.shiro.AuthUser;
import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import com.letv.woodpecker.wpwebapp.entity.TableData;
import com.letv.woodpecker.wpwebapp.entity.TableHeader;
import com.letv.woodpecker.wpwebapp.itf.ApplicationService;
import com.letv.woodpecker.wpwebapp.vo.ExceptionVo;
import com.mongodb.DBObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhusheng on 17/3/22.
 * modify by meijunjie
 * @author zhusheng
 */
@Controller
@RequestMapping(value = "/woodpecker/exception")
public class ExceptionInfoController extends BaseController
{
    @Resource
    private ExceptionService exceptionService;
    @Resource(name = "applicationServiceWeb")
    private ApplicationService applicationService;


    private RedisTemplate redisTemplate;

    /**
     * StringRedisSerializer 用于序列化 字符
     * RedisTemplate默认使用JdkSerializationRedisSerializer 进行序列化，它有个缺点就是生成的序列化文件可读性差，而且占用空间大
     * GenericJackson2JsonRedisSerializer，可以将对象序列化成JSON格式存储在redis中
     * @param redisTemplate
     */
    @Autowired(required = false)
    @SuppressWarnings(value = "unchecked")
    public void setRedisTemplate(RedisTemplate redisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(stringSerializer);
        this.redisTemplate = redisTemplate;
    }


    @RequestMapping("/toAllExceptionsPage")
    public String toAllExceptionsPage(ModelMap model, HttpServletRequest request)
    {
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        String userId = authUser.getLoginName();
        List<AppInfo> appInfos = applicationService.queryAllApps(userId, 0, Integer.MAX_VALUE);
        model.put("appInfos",appInfos);
        return "exception/exceptionlist";
    }

    @RequestMapping(value = "/queryAllExceptionsPage",method = RequestMethod.POST)
    public Map<String,Object> queryAllExceptionsPage(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String,String> params = valueMap.toSingleValueMap();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        String userId = params.get("username");
        String appName = params.get("appName");
        String startTime = params.get("startTime");
        if(startTime == null){
            startTime = "";
        }
        String endTime = params.get("endTime");
        if(endTime == null){
            endTime = "";
        }
        List<ExceptionInfo> exceptionInfoList = exceptionService.queryAllExceptions(userId,appName,startTime,endTime,pageStart,pageSize,getAppNamesByUserId());
        long count = exceptionService.getAllExceptionCount(userId,appName,startTime,endTime,getAppNamesByUserId());
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count); //totalAfterFilter
        result.put("data", exceptionInfoList.toArray());
        return result;
    }

    @RequestMapping(value = "/toExceptionVersion2Page", method = RequestMethod.GET)
    public String toExceptionVersion2Page(ModelMap model, HttpServletRequest request){
        String userId = getUseId(request);
        List<AppInfo> appInfos = applicationService.queryAllApps(userId,0,Integer.MAX_VALUE);
        model.put("appInfos",appInfos);
        return "exception/exception_version2";
    }

    /**
     * db.exceptionInfo.group({
     key: {"exceptionType": true},
     initial: {"appName":"","count": 0},
     reduce: function(doc, out){
     out.count++;out.appName=doc.appName;
     },
     finalize: function(out){
     return out;
     },
     condition:{$and:[{"appName":"mini-ecommerce"},{"logTime": {$gt: "2017-07-08 13:24:47"}}]}
     })

     db.exceptionInfo.mapReduce(
     function() { emit(this.appName+"_"+this.exceptionType,1); },
     function(key, values) {return Array.sum(values)},
     {
     query:{"logTime": {$gt: "2017-07-08 13:24:47"}},
     out:"post_total"
     }
     ).find()
     sql 优化
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryExceptionPage", method = RequestMethod.POST)
    public Map<String, Object> queryExceptionPage(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String,String> params = valueMap.toSingleValueMap();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        String userId = params.get("username");
        String appName = params.get("appName");
        String startTime = params.get("startTime");
        if(startTime == null){
            startTime = "";
        }
        String endTime = params.get("endTime");
        if(endTime == null){
            endTime = "";
        }
        List<DBObject> dbObjects = exceptionService.queryListByScheduler(userId,appName,startTime,endTime,getAppNamesByUserId());
        List<ExceptionVo> exceptionVos = new ArrayList<>();
        for(int i=pageStart; i<dbObjects.size() && i<pageStart+pageSize; i++){
            ExceptionVo vo = new ExceptionVo();
            vo.setAppName(dbObjects.get(i).get("appName").toString());
            vo.setExceptionType(dbObjects.get(i).get("exceptionType").toString());
            vo.setCount(Float.valueOf( dbObjects.get(i).get("count").toString() ).intValue());
            exceptionVos.add(vo);
        }
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", dbObjects.size());
        result.put("iTotalDisplayRecords", dbObjects.size()); //totalAfterFilter
        result.put("data", exceptionVos.toArray());
        return result;
    }

    @RequestMapping(value = "/classifyByMd5", method = RequestMethod.POST)
    public Map<String,Object> classifyByMd5(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String,String> params = valueMap.toSingleValueMap();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        String appName = params.get("appName");
        String startTime = params.get("startTime");
        String endTime = params.get("endTime");
        String exceptionType = params.get("exceptionType");
        List<DBObject> dbObjects = exceptionService.classifyByMd5(appName,startTime,endTime,exceptionType);
        List<ExceptionVo> exceptionVos = new ArrayList<>();
        for(int i = pageStart; i<dbObjects.size() && i<pageStart+pageSize; i++){
            ExceptionVo vo = new ExceptionVo();
            vo.setAppName(dbObjects.get(i).get("appName").toString());
            vo.setExceptionType(exceptionType);
            vo.setContentMd5(dbObjects.get(i).get("contentMd5").toString());
            String msg = dbObjects.get(i).get("msg").toString();
            vo.setContent(msg);
            vo.setCount(Float.valueOf( dbObjects.get(i).get("count").toString() ).intValue());
            exceptionVos.add(vo);
        }
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", dbObjects.size());
        result.put("iTotalDisplayRecords", dbObjects.size()); //totalAfterFilter
        result.put("data", exceptionVos.toArray());
        return result;

    }

    @RequestMapping(value = "/exceptionListByDetail", method = RequestMethod.POST)
    public Map<String,Object> exceptionListByDetail(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String,String> params = valueMap.toSingleValueMap();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));
        String appName = params.get("appName");
        String startTime = params.get("startTime");
        String endTime = params.get("endTime");
        String exceptionType = params.get("exceptionType");
        String contentMd5 = params.get("contentMd5");
        List<ExceptionInfo> results = exceptionService.queryList(appName,exceptionType,contentMd5,startTime,endTime,pageStart,pageSize);
        long count = exceptionService.getCountByDetail(appName,exceptionType,contentMd5,startTime,endTime);
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count); //totalAfterFilter
        result.put("data", results.toArray());
        return result;
    }

    @RequestMapping(value = "/toExceptionChartsPage",method = RequestMethod.GET)
    public String toExceptionChartsPage(ModelMap modelMap){


        return "exception/exception_charts";
    }

    @RequestMapping(value = "/exceptionNumChart")
    public Map<String,Object> exceptionNumChart(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response, HttpServletRequest request){
        Map<String,String> params = valueMap.toSingleValueMap();
        String userId = getUseId(request);
        String startTime = params.get("startTime");
        if(startTime==null || "".equals(startTime)){
            startTime = nowTime(true);
        }
        String endTime = params.get("endTime");
        if(endTime==null || "".equals(endTime)) {
            endTime = nowTime(false);
        }
        List<DBObject> exceptionNums = exceptionService.findExceptionNumByApp(userId,startTime,endTime,getAppNamesByUserId());
        List<ExceptionVo> vos = new ArrayList<>();
        for(int i = 0;i<exceptionNums.size(); i++){
            ExceptionVo vo = new ExceptionVo();
            vo.setAppName(exceptionNums.get(i).get("appName").toString());
            vo.setCount(Float.valueOf( exceptionNums.get(i).get("count").toString() ).intValue());
            vos.add(vo);
        }
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("data",vos.toArray());
        return result;
    }

    @RequestMapping(value = "/exceptionNumPieChart")
    public Map<String,Object> exceptionNumPieChart(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response, HttpServletRequest request){
        Map<String,String> params = valueMap.toSingleValueMap();
        String userId = getUseId(request);
        String startTime = params.get("startTime");
        if(startTime==null || "".equals(startTime)){
            startTime = nowTime(true);
        }
        String endTime = params.get("endTime");
        if(endTime==null || "".equals(endTime)) {
            endTime = nowTime(false);
        }
        List<DBObject> exceptionNums = exceptionService.findExceptionNumByApp(userId,startTime,endTime,getAppNamesByUserId());
        List<ExceptionVo> vos = new ArrayList<>();
        for(int i = 0;i<exceptionNums.size(); i++){
            ExceptionVo vo = new ExceptionVo();
            vo.setAppName(exceptionNums.get(i).get("appName").toString());
            vo.setCount(Float.valueOf( exceptionNums.get(i).get("count").toString() ).intValue());
            vos.add(vo);
        }
        //按异常类型分组
        List<DBObject> dbObjects = exceptionService.queryListByScheduler(userId,null,startTime,endTime,getAppNamesByUserId());
        List<ExceptionVo> exceptionVos = new ArrayList<>();
        for(int i = 0;i<dbObjects.size(); i++){
            ExceptionVo vo = new ExceptionVo();
            vo.setAppName(dbObjects.get(i).get("appName").toString());
            vo.setExceptionType(dbObjects.get(i).get("exceptionType").toString());
            vo.setCount(Float.valueOf( dbObjects.get(i).get("count").toString() ).intValue());
            exceptionVos.add(vo);
        }
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("data",vos.toArray());
        result.put("exceptionType",exceptionVos.toArray());
        return result;
    }

    @GetMapping(value="/realExceptionPerMinute")
    public String realExceptionPerMinute(HttpServletRequest request, ModelMap modelMap){
        String userId = getUseId(request);
        List<AppInfo> appInfos = applicationService.queryAllApps(userId,0,Integer.MAX_VALUE);
        modelMap.addAttribute("appInfos",appInfos);
        return "exception/real_time_exception_minute";
    }

    @GetMapping(value="/realExceptionPerHour")
    public String realExceptionPerHour(HttpServletRequest request, ModelMap modelMap){
        String userId = getUseId(request);
        List<AppInfo> appInfos = applicationService.queryAllApps(userId,0,Integer.MAX_VALUE);
        modelMap.addAttribute("appInfos",appInfos);
        return "exception/real_time_exception_hour";
    }


    @GetMapping(value="/realExceptionPerDay")
    public String realExceptionPerDay(HttpServletRequest request, ModelMap modelMap){
        String userId = getUseId(request);
        List<AppInfo> appInfos = applicationService.queryAllApps(userId,0,Integer.MAX_VALUE);
        modelMap.addAttribute("appInfos",appInfos);
        return "exception/real_time_exception_day";
    }



    /**
     * 按应用查询
     * @param appName  应用名
     * @param type     type /按分统计 按小时统计
     * @param response 输出响应
     */
    @RequestMapping(value = "/dynamic/info/{appName}/{type}", method = {RequestMethod.POST, RequestMethod.GET})
    @SuppressWarnings(value="unchecked")
    public void chartException(@PathVariable String appName, @PathVariable String type, HttpServletResponse response){
        List results = new ArrayList();
        Map columns = new HashMap(4);
        List columnsTemp = new ArrayList();
        List<TableHeader> tableHeaders = new ArrayList<>(4);
        TableData tableData = new TableData();
        List<Map<String,String>> rows = new ArrayList<>(2);
        getTableHeaderAndData(appName,tableHeaders,rows,type);
        tableData.setTotal(rows.size());
        columnsTemp.add(tableHeaders);
        columns.put("columns",columnsTemp);
        tableData.setRows(rows);
        results.add(columns);
        results.add(tableData);
        printJSON(response, results);
    }


    /**
     * 确定表头
     * @param appName   应用名
     */
    @SuppressWarnings(value = "unchecked")
    private void getTableHeaderAndData(String appName, List<TableHeader> tableHeaders, List<Map<String,String>> tableData, String type){
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();
        String appExceptionKey = appName + "_exception_set";
        Set<String> allExceptionTypes = setOperations.members(appExceptionKey);
        String[] allExceptionTypeArray = allExceptionTypes.toArray(new String[allExceptionTypes.size()]);
        tableHeaders.add(new TableHeader("时间",50,"center","zAxle"));
        //获取表头
        for(int i = 0; i < allExceptionTypeArray.length; i++){
            tableHeaders.add(new TableHeader(allExceptionTypeArray[i],50,"center","x_" + i));
        }
        getTableData(appName, allExceptionTypeArray, tableData, type);
    }

    private void getTableData(String appName, String[] allExceptionTypeArray, List<Map<String,String>> tableData, String type){
        final String perMinute = "perMinute";
        final String perHour = "perHour";
        final String perDay = "perDay";

        String now;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer count;

        // 按分钟生成数据
        switch (type) {
            case perMinute:
                count = 20;
                break;
            case perHour:
                count = 24;
                break;
            case perDay:
                count = 20;
                break;
            default: count=20;
        }

        for(int i = 0; i < count; i++){
            switch (type) {
                case perMinute:
                    now = DateFormatUtils.format(System.currentTimeMillis() - i * 60 * 1000, "yyyy-MM-dd HH:mm:ss");
                    now = now.substring(now.indexOf("-") + 1, now.lastIndexOf(":"));
                    break;
                case perHour:
                    now = DateFormatUtils.format(System.currentTimeMillis() - i * 60 * 60 * 1000, "yyyy-MM-dd HH:mm:ss");
                    now = now.substring(now.indexOf("-") + 1, now.indexOf(":"));
                    break;
                default:
                    now = DateFormatUtils.format(System.currentTimeMillis() - i * 60 * 60 * 24 * 1000, "yyyy-MM-dd HH:mm:ss");
                    now = now.substring(now.indexOf("-") + 1, now.indexOf(" ") + 1);
                    break;
            }
            Map<String,String> rowData = new HashMap<>(allExceptionTypeArray.length + 1);

            if(now.endsWith(" ")){
                rowData.put("zAxle",now);
            }else {
                rowData.put("zAxle",now.contains(":")? now : now+":00");
            }
            for(int j = 0; j < allExceptionTypeArray.length; j++){
                String nowTemp = now;
                String keyPerMinute = appName + "_" + allExceptionTypeArray[j] + "_" + now.replace(" ","-");
                rowData.put("x_" + j, dealExceptionDetail(appName,allExceptionTypeArray[j],(String) valueOperations.get(keyPerMinute),nowTemp));
            }
            tableData.add(rowData);
        }
    }


    private String dealExceptionDetail(String appName, String exceptionType, String exceptionNum, String time){
        if(StringUtils.isNotBlank(exceptionNum)){
            exceptionNum = "<a href='/woodpecker/exception/realtimeException/" + appName +"/" + exceptionType + "/" + time  + "'" + ">" + "<font color='red'>" + exceptionNum + "</font>" + "</a>";
        }
        return exceptionNum;
    }


    @RequestMapping(value = "/realtimeException/{appName}/{exceptionType}/{logTime}", method = {RequestMethod.GET, RequestMethod.POST})
    public String toRealTimeExceptionDetail(@PathVariable String appName, @PathVariable String exceptionType, @PathVariable String logTime, ModelMap modelMap){
        modelMap.addAttribute("appName",appName);
        modelMap.addAttribute("exceptionType",exceptionType);
        modelMap.addAttribute("logTime",logTime);
        return "exception/real_time_exception_list";
    }


    /**
     *
     * @param valueMap
     * @param response
     * @return
     */
    @RequestMapping(value = "/queryRealTimeException", method = {RequestMethod.GET,RequestMethod.POST})
    public Map<String,Object> queryRealTimeException(@RequestBody MultiValueMap<String,String> valueMap, HttpServletResponse response){
        Map<String,String> params = valueMap.toSingleValueMap();
        int pageStart = Integer.valueOf(params.get("iDisplayStart"));
        int pageSize = Integer.valueOf(params.get("iDisplayLength"));

        String appName = params.get("appName");
        String logTime = params.get("logTime");
        String exceptionType = params.get("exceptionType");
        String startTime = null;
        String endTime = null;
        String now = DateFormatUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        String year = StringUtils.isNotBlank(now) ? now.split("-")[0] : "2018";
        // 按分钟查询
        if(logTime.contains(":")){
            startTime = year + "-" + logTime + ":00";
            endTime = year + "-" + logTime + ":59";
        }else {
            // 按小时查询
            if(logTime.contains(" ")){
                startTime = year + "-" + logTime + ":00" + ":00";
                endTime = year + "-" + logTime + ":59" + ":59";
            }else {
                // 按天查询
                startTime = year + "-" + logTime + " 00:00:00";
                endTime = year + "-" + logTime + " 23:59:59";
            }
        }
        List<ExceptionInfo> exceptionInfoList = exceptionService.queryList(appName,exceptionType,null,startTime,endTime,pageStart,pageSize);
        long count = exceptionService.getCountByDetail(appName,exceptionType,null,startTime,endTime);
        Map<String,Object> result = getSuccessMap();
        setResContent2Json(response);
        result.put("iTotalRecords", count);
        result.put("iTotalDisplayRecords", count);
        result.put("data", exceptionInfoList.toArray());
        return result;
    }




    /**
     * 获取当前时间
     * @param isZeroHour  true : 返回今日零点，false:返回当前时间
     *
     * */
    private String nowTime(boolean isZeroHour){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(isZeroHour){
            format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        }
        Date now = new Date();
        return format.format(now);
    }


    /**
     * 查询当前登录用户名下的应用
     * @return           所有应用名
     */
    private List<String> getAppNamesByUserId(){
        AuthUser authUser = (AuthUser) SecurityUtils.getSubject().getPrincipal();
        List<AppInfo> appInfos = applicationService.queryAllApps(authUser.getLoginName(),0,Integer.MAX_VALUE);
        List<String> appNames = new ArrayList<>(4);
        for(AppInfo appInfo : appInfos){
            appNames.add(appInfo.getAppName());
        }
        return appNames;
    }

}
