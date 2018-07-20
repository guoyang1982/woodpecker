package com.letv.woodpecker.wpdatamodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/7/12 下午5:48
 */
@Data
public class RuleConfig implements Serializable {
    private String _id;
    private String ruleId;
    private String userName;
    private String appName;
    private String ruleName;
    private String ruleDesc;
    private String ruleConfig;
    private String exceptionInfo;
    private String cTime;
    private String mTime;
}
