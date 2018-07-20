package com.letv.woodpecker.wpwebapp.itf;

import com.letv.woodpecker.wpwebapp.entity.User;

import java.util.List;

/**
 * 用户信息操作接口
 * @author meijunjie @date 2018/7/5
 */
public interface UserService {
    /**
     * 保存用户信息
     * @param user          用户信息
     */
    void saveUser(User user);

    /**
     * 更新用户信息
     * @param user          用户信息
     */
    void update(User user);

    /**
     * 查询用户信息
     * @param loginName     登录名
     * @return              用户信息
     */
    User  queryByLoginName(String loginName);

    /**
     * 批量查询用户信息
     * @param userStatus    用户状态 0停用 1启用
     * @param userRole      用户角色 1超级管理员 2项目所有者 3普通开发者
     * @param page          分页数
     * @param pageSize      分页大小
     * @return              用户信息
     */
    List<User>  queryAll(Integer userStatus, Integer userRole, Integer page, Integer pageSize);

    /**
     * 按用户状态和角色统计总数
     * @param userStatus    用户状态
     * @param userRole      用户角色
     * @return              用户总数
     */
    Long countAll(Integer userStatus, Integer userRole);


    /**
     * 按登录名删除
     * @param loginName     登录名
     */
    void deleteUser(String loginName);

    /**
     * 更新用户状态
     * @param user          用户信息
     */
    void updateUserStatus(User user);
}
