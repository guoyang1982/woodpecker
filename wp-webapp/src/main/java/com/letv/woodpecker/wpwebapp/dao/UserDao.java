package com.letv.woodpecker.wpwebapp.dao;

import com.letv.woodpecker.wpwebapp.entity.User;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

/**
 * 用户信息操作接口
 * @author meijunjie @date 2018/7/4
 */
@DB(table = "t_user")
public interface UserDao {

    /**
     * 保存用户信息
     * @param user      用户信息实体
     */
    @SQL("insert into #table(login_name,user_name,password,salt,user_role,user_status,mobile_phone,mail,create_time,modify_time) " +
            "values(:loginName,:userName,:password,:salt,:userRole,:userStatus,:mobilePhone,:mail,:createTime,:modifyTime)")
    void saveUser(User user);

    /**
     * 按登录名获取用户详细信息
     * @param loginName 登录名
     * @return          用户信息
     */
    @SQL("select * from #table where login_name=:1")
    User getByLoginName(String loginName);

    /**
     * 可修改项
     * password          密码
     * mobile_phone      手机号 用于接收告警短信
     * mail              邮箱  用于接收告警邮件
     * department        部门信息
     * user_status       用户状态 0停用 1启用
     * user_role         用户角色 1超级管理员 2应用所有者 3普通用户
     * @param user       用户信息实体
     */
    @SQL("update #table set user_name=:userName,password=:password,user_role=:userRole,user_status=:userStatus,salt=:salt,mobile_phone=:mobilePhone,mail=:mail,modify_time=:modifyTime" +
            " where login_name=:loginName")
    void updateUser(User user);

    /**
     * 按用户状态和角色查询所有用户信息，支持分页查询
     * @param status    用户状态
     * @param userRole  用户角色
     * @param page      分页数
     * @param pageSize  分页大小
     * @return          用户信息
     */
    @SQL("select * from #table where" +
            "#if(:1!=null) user_status=:1  and #end" +
            "#if(:2!=null) user_role=:2  and #end" +
            " 1=1 order by modify_time desc limit :3,:4")
    List<User> getAllUsersByStatus(Integer status, Integer userRole, Integer page, Integer pageSize);

    /**
     * 按用户状态和角色统计总数
     * @param status    用户状态
     * @param userRole  用户角色
     * @return          用户总数
     */
    @SQL("select count(1) from #table where" +
            "#if(:1!=null) user_status=:1 and #end" +
            "#if(:2!=null) user_role=:2 and #end 1=1")
    Long countAll(Integer status, Integer userRole);

    /**
     * 按用户名删除
     * @param loginName 用户名
     */
    @SQL("delete from #table where login_name=:1")
    void deleteByLoginName(String loginName);
}
