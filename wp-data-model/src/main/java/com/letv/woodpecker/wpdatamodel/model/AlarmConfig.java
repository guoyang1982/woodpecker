package com.letv.woodpecker.wpdatamodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhusheng on 17/3/16.
 * @author zhusheng
 */
@Data
public class AlarmConfig implements Serializable{

    private static final long serialVersionUID = -8841984191517924171L;

    private String _id;
    private String alarmId;
    private String userId;
    private String appName;
    private String ip;
    private String exceptionType;

    /** 全局配置GLOBAL 普通配置NORMAL或者NULL 默认是普通配置*/
    private String configType;

    /**规则ID */
    private String ruleId;

    /** 告警配置倍率*/
    private Double multiple;


    /**
     * 告警阈值
     */
    private Integer threshold;
    private String email;
    private String phoneNum;
    /**
     * 告警频率
     */
    private Integer alarmFrequency;

    /**
     * 每个企业都拥有唯一的corpid，获取此信息可在管理后台“我的企业”－
     * “企业信息”下查看“企业ID”（需要有管理员权限）
     */
    private String corpid;
    /**
     * secret是企业应用里面用于保障数据安全的“钥匙”，每一个应用都有一个独立的访问密钥，
     * 为了保证数据的安全，secret务必不能泄漏。
     目前secret有：
     自建应用secret。在管理后台->“企业应用”->“自建应用”，点进某个应用，即可看到。
     */
    private String secret;
    /**
     * 部门ID:每个部门都有唯一的id，在管理后台->“通讯录”->“组织架构”->点击某个部门右边的小圆点可以看到
     */
    private String toparty;
    /**
     * 每个应用都有唯一的agentid。在管理后台->“企业应用”->点进应用，即可看到agentid
     */
    private String agentid;

}
