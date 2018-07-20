package com.letv.woodpecker.wpwebapp.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 应用用户映射关系
 * @author meijunjie @date 2018/7/12
 */
@Getter
@Setter
public class UserApp implements Serializable {
    private static final long serialVersionUID = 1619077362027170053L;

    /** 自增主键*/
    private Long id;
    /** 用户ID*/
    private Long userId;
    /** 应用ID*/
    private String appId;
    /** 当前状态 */
    private Integer status;
    /** 创建时间*/
    private Date createTime;
}
