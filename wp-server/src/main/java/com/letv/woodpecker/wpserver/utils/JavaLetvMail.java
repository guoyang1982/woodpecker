package com.letv.woodpecker.wpserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leeco
 */
public class JavaLetvMail {
	private static Logger logger = LogManager.getLogger(JavaLetvMail.class);

	public static void doSendHtmlEmail(String subject, String sendHtml, String receiveUser,String ccUsers, File attachment) {

		try {
			if (!StringUtils.isEmpty(receiveUser)) {
				Map<String,String> mailParam = new HashMap<String,String>();
				mailParam.put("subject", subject);
				mailParam.put("content", sendHtml);
				mailParam.put("recevier", receiveUser);
				HttpUtils.post("http://10.110.98.129/notify/mail",null, mailParam, 3000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
