package com.letv.woodpecker.wpwebapp.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用信息实体Bean
 * @author meijunjie @date 2018/7/10
 */
@Getter
@Setter
public class AppInfo implements Serializable {
    private static final long serialVersionUID = -6475556580720437975L;

    /** 自增主键，应用ID*/
    private String _id;
    /** 应用名*/
    private String appName;
    /** 应用状态 1启用 0停用*/
    private Integer status;
    /** 应用创建者*/
    private String creator;
    /** 该应用对应的IP,以;分隔*/
    private String ip;
    /** 应用创建时间*/
    private Date createTime;
    /** 应用修改时间*/
    private Date modifyTime;

}
