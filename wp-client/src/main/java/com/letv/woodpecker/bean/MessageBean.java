package com.letv.woodpecker.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhusheng on 17/3/13.
 * modified by meijunjie on 17/9/26
 * this pojo used to encapsulate error message
 */
@Data
public class MessageBean implements Serializable{

    /**
     * the name of application
     */
    private String appName ;

    /**
     * the host of application
     */
    private String ip;

    /**
     * the error message of the application
     */
    private String msg;


    /**
     * the time of error occurrence
     */
    private String createTime;
}
