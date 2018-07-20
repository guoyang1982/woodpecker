package com.letv.woodpecker.wpwebapp.itf.impl;

import com.letv.woodpecker.wpwebapp.dao.UserDao;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户操作实现类
 * @author meijunjie @date 2018/7/5
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public void saveUser(User user) {
        userDao.saveUser(user);
    }

    @Override
    public void update(User user) {
        userDao.updateUser(user);
    }

    @Override
    public User queryByLoginName(String loginName) {
        return userDao.getByLoginName(loginName);
    }

    @Override
    public List<User> queryAll(Integer userStatus, Integer userRole, Integer page, Integer pageSize) {
        return userDao.getAllUsersByStatus(userStatus, userRole, page, pageSize);
    }

    @Override
    public Long countAll(Integer userStatus, Integer userRole) {
        return userDao.countAll(userStatus, userRole);
    }

    @Override
    public void deleteUser(String loginName) {
        userDao.deleteByLoginName(loginName);
    }

    @Override
    public void updateUserStatus(User user) {
        userDao.updateUserStatus(user);
    }
}
