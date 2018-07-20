package com.letv.woodpecker.wpwebapp.itf;

import com.letv.woodpecker.wpwebapp.entity.UserApp;


import java.util.List;


/**
 * 用户应用映射关系
 * @author meijunjie @date 2018/7/12
 */
public interface UserAppService {

    /**
     * 批量获取用户ID下的应用映射关系
     * @param userId              应用ID
     * @return                    用户应用映射关系
     */
    List<UserApp> queryList(Long userId);
}
