package com.letv.woodpecker.wpwebapp.dao;

import com.letv.woodpecker.wpwebapp.entity.UserApp;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.ReturnGeneratedId;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

/**
 * TODO appId变更为mongDB主键
 * @author meijunjie @date 2018/7/12
 */
@DB(table = "t_user_app")
public interface UserAppDao {

    /**
     * 记录用户应用映射关系
     * @param userApp        用户应用映射实体
     * @return               自增主键
     */
    @ReturnGeneratedId
    @SQL("insert into #table(user_id,app_id,status,create_time) values(:userId,:appId,:status,:createTime)")
    Long insertUserAppMapping(UserApp userApp);

    /**
     * 删除用户应用映射关系
     * @param userId         用户ID
     * @param appId          应用ID
     */
    @SQL("delete from #table where user_id=:1 and app_id=:2")
    void delete(Long userId, String appId);

    /**
     * 按应用ID删除用户应用映射关系
     * @param appId          应用ID
     */
    @SQL("delete from #table where app_id=:1")
    void delete(String appId);


    /**
     * 查询某个用户名下所有的应用
     * @param userId         用户ID
     * @return               所有应用映射关系
     */
    @SQL("select * from #table where user_id=:1  order by create_time desc limit :2,:3")
    List<UserApp>  queryAllByUserId(Long userId, Integer pageStart, Integer pageSize);

    /**
     * 统计按用户名下挂载的应用总数
     * @param userId          用户ID
     * @return                该用户关联应用数
     */
    @SQL("select count(1) from #table where user_id=:1")
    Integer queryCountByUserId(Long userId);

    /**
     * 查询某个应用下所有的用户
     * @param appId          应用ID
     * @return               所有映射关系
     */
    @SQL("select * from #table where app_id=:1")
    List<UserApp> queryAllByAppId(String appId);

    /**
     * 统计当前应用下用户总数
     * @param appId          应用ID
     * @return               该应用下挂载的用户总数
     */
    @SQL("select count(1) from #table where app_id=:1")
    Long queryAllCountByAppId(String appId);

    /**
     * 按用户ID，应用Id查询相关信息
     * @param userId         用户ID
     * @param appId          应用ID
     * @return               用户应用映射关系
     */
    @SQL("select * from #table where user_id=:1 and app_id=:2")
    UserApp queryByUserIdAndAppId(Long userId, String appId);

    /**
     * 批量获取当前用户名下的应用
     * @param userId         用户ID
     * @return               应用信息
     */
    @SQL("select * from #table where user_id=:1")
    List<UserApp> queryListByUserId(Long userId);

}
