package com.letv.woodpecker.wpwebapp.vo;

import lombok.Data;

/**
 * Created by zhusheng on 17/3/27.
 * @author zhusheng
 */
@Data
public class ExceptionVo {
    private String appName;
    private String ip;
    private String exceptionType;
    private String startTime;
    private String endTime;
    private String content;
    private String contentMd5;
    private int count;
    private int total;
}
