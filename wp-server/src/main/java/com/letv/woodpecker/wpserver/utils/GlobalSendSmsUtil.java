package com.letv.woodpecker.wpserver.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送短信工具类 copy from alarm project
 * @author meijunjie
 */
@Slf4j
public class GlobalSendSmsUtil {
  private final static String SEND_SMS_URL = "http://10.110.98.129/notify/monitorPhone";
  private static final String ENCODE_VALUE = Charset.forName("UTF-8").toString();
  private static final Logger logger = LoggerFactory.getLogger(GlobalSendSmsUtil.class);
  private static final String USER = "lssc-01-zf-01";
  private static final String PWD = "23f85s20";

  /**
   * 验证国内号码
   * @param phone
   * @return
   */
  private static boolean telephoneRegexMatches(String phone) {
    String pattern = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(phone);
    return m.matches();
  }

  /**
   * 发送短信接口
   *
   * @param phoneNum  手机号
   * @param msg       报警信息
   * @return          调用结果
   */
  public static boolean sendMessage(String phoneNum, String msg) {
    if (StringUtils.isBlank(msg) || StringUtils.isBlank(phoneNum)) {
      return false;
    }

    try {
      Map<String, String> paramsMap = new HashMap<String, String>();
      paramsMap.put("to", phoneNum);
      paramsMap.put("sendmsg", msg);
      String outputJsonData = HttpUtils.post(SEND_SMS_URL, null,paramsMap, 3000);

      log.info("end msg:,phoneNum={},msg={},res={}",phoneNum, msg, outputJsonData);

      if (StringUtils.isNotBlank(outputJsonData)) {
        return false;
      }
      JSONObject outObj = JSONObject.parseObject(outputJsonData);
      System.out.println(outObj);
      String statusCode = "statuscode";
      String statusCodeValue = "000000";
      return outObj.getString(statusCode).equals(statusCodeValue);
    } catch (Exception e) {
      logger.error("sendSms error", e);
      return false;
    }
  }
}
