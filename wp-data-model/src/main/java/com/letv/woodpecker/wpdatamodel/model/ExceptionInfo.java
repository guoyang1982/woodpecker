package com.letv.woodpecker.wpdatamodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhusheng on 17/3/16.
 */
@Data
public class ExceptionInfo implements Serializable{
    private String appName;
    private String ip;
    private String exceptionType;
    private String createTime;
    private String msg;
    private String logTime;
    private String contentMd5;
}
