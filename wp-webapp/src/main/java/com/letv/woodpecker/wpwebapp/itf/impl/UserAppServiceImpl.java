package com.letv.woodpecker.wpwebapp.itf.impl;

import com.letv.woodpecker.wpwebapp.dao.UserAppDao;
import com.letv.woodpecker.wpwebapp.entity.UserApp;
import com.letv.woodpecker.wpwebapp.itf.UserAppService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author meijunjie
 * @date 2018/7/16
 */
@Service("userAppService")
public class UserAppServiceImpl implements UserAppService {

    @Resource
    private UserAppDao userAppDao;

    @Override
    public List<UserApp> queryList(Long userId) {
        return userAppDao.queryListByUserId(userId);
    }
}
