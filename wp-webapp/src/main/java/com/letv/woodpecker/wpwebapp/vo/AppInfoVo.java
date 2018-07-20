package com.letv.woodpecker.wpwebapp.vo;

import com.letv.woodpecker.wpwebapp.entity.AppInfo;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author meijunjie
 * @date 2018/7/13
 */
@Getter
@Setter
public class AppInfoVo extends AppInfo {

    private static final long serialVersionUID = -8445472024869891453L;

    /** 当前应用受邀用户*/
    private String invitees;
}
