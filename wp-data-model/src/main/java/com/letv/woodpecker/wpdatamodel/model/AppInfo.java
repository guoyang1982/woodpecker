package com.letv.woodpecker.wpdatamodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by zhusheng on 17/3/16.
 */
@Data
public class AppInfo implements Serializable {
    private String userId;
    private String appName;
    private String ip;
}
