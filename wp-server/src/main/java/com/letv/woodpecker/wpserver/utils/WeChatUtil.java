package com.letv.woodpecker.wpserver.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.letv.woodpecker.wpserver.cache.LocalCacheBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author guoyang
 * @Description: 微信公众号报警工具类
 * @date 2018/7/9 下午2:11
 */
@Slf4j
public class WeChatUtil extends LocalCacheBaseService<String, String> {
    private static WeChatUtil weChatUtil = new WeChatUtil();
    private String url = "https://qyapi.weixin.qq.com/cgi-bin";

    private WeChatUtil() {
        super(100, TimeUnit.MINUTES);
    }

    public static WeChatUtil getInstance() {
        return weChatUtil;
    }


    public String authId(String corpid,String secret) {
        String tocken = getCache(corpid + "@_@" + secret);
        log.info("the tocken={}",tocken);

        return tocken;
    }

    public String getToken(String url) {
        String res = "";
        try {
            res = HttpUtils.get(url, null, 3000);
        } catch (Exception e) {
            log.error("get tocken exceptione:e={}",e);

        }
        return res;
    }

    public void sendMessage(String corpid,String secret,String toparty,String agentid,String toUser, String messages) {
        //验证
        String accToken = authId(corpid,secret);
        if (StringUtils.isBlank(accToken)) {
            return;
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("touser", toUser);
        dataMap.put("toparty", toparty);
        dataMap.put("msgtype", "text");
        dataMap.put("agentid", agentid);
        Map<String, String> content = Maps.newHashMap();
        content.put("content", messages);
        dataMap.put("text", content);
        dataMap.put("safe", "0");

        try {
            String res = HttpUtils.post(url +
                    "/message/send?access_token=" + accToken, null, JSON.toJSONString(dataMap), 3000);

            log.info(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String loadData(String s) {
        String pare[] = s.split("@_@");
        String res = getToken(url + "/gettoken?corpid=" + pare[0] + "&corpsecret=" + pare[1]);
        if (StringUtils.isNotBlank(res)) {
            JSONObject obj = JSON.parseObject(res);
            if(null != obj.get("access_token")){
                return (String) obj.get("access_token");
            }else {
                log.error("get tocken fail,res={}",res);
            }

        }
        return "";
    }
}
