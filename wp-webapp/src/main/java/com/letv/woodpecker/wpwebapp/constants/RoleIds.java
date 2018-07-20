package com.letv.woodpecker.wpwebapp.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 角色ID
 * 1 超级管理员
 * 2 应用所有者
 * 3 普通用户
 * @author meijunjie @date 2018/7/4
 */
public class RoleIds {
    /** 超级管理员*/
    public static final int SUPER_ADMINER = 1;
    /** 应用所有者*/
    public static final int APPLICATION_OWNER = 2;
    /** 普通用户，默认所有用户都是不同用户*/
    public static final int NORMAL_USER = 3;

    public static final Map<Integer,String> roleName = new HashMap<>(3);
    static {
        roleName.put(1,"超级管理员");
        roleName.put(2,"应用所有者");
        roleName.put(3,"普通用户");
    }
}
