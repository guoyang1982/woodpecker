package com.letv.woodpecker.wpwebapp.entity;

import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

/**
 * wp-web系统用户信息实体
 * @author meijunjie @date 2018/7/3
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1421445534312648473L;
    /** 主键*/
    private Long id;
    /** 用户登录名*/
    @NotBlank
    private String loginName;
    /** 用户名*/
    private String userName;
    /** 密码密文存储*/
    @NotBlank
    private String password;
    /** 加密用的盐*/
    private String salt;
    /** 手机号*/
    private String mobilePhone;
    /** 邮箱*/
    private String mail;
    /** 用户角色*/
    private Integer userRole;
    /** 用户状态 1启用 0停用 默认启用*/
    private Integer userStatus = 1;
    /** 所属部门*/
    private String department;
    /** 创建时间*/
    private Date createTime;
    /** 修改时间*/
    private Date modifyTime;
}
