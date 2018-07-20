package com.letv.woodpecker.wpwebapp.auth.shiro;

import com.letv.woodpecker.wpwebapp.constants.UserStatus;
import com.letv.woodpecker.wpwebapp.entity.User;
import com.letv.woodpecker.wpwebapp.itf.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.mockito.internal.util.collections.Sets;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author meijunjieb @date 2018/7/3
 * TODO 做成可插拔的形式，可以使用wp内部维护的用户信息，也可以使用目标用户内部SSO服务
 */
@Slf4j
public class WpWebRealm extends AuthorizingRealm {

    static final Map<Integer,String> roles = new HashMap<>(3);
    static {
        roles.put(1,"admin");
        roles.put(2,"applicationOwner");
        roles.put(3,"normal");
    }

    @Resource
    private UserService userService;

    public WpWebRealm(CacheManager cacheManager,CredentialsMatcher credentialsMatcher) {
        super(cacheManager,credentialsMatcher);
    }

    /**
     * 用户身份认证
     * @param authenticationToken       认证令牌，由shiro封装
     * @return                          用户身份认证信息
     * @throws AuthenticationException  用户身份认证异常
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {
        log.info("Shiro start login authentication. date={}",new Date());
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;

        // 此处可做成可插拔的形式
        User user = userService.queryByLoginName(token.getUsername());
        // 账号不存在
        if(user == null){
            return null;
        }
        // 账号未启用
        if(user.getUserStatus() == UserStatus.ACCOUNT_CLOSED){
            return null;
        }

        // 读取用户角色
        AuthUser authUser = new AuthUser();
        authUser.setRole(user.getUserRole());
        authUser.setLoginName(user.getLoginName());
        authUser.setName(user.getUserName());
        authUser.setId(user.getId());

        return new SimpleAuthenticationInfo(authUser,user.getPassword().toCharArray(),
                ShiroByteSource.of(user.getSalt()),getName());
    }

    /**
     * 授权
     * @param principals    用拥有指定权限
     * @return              获取授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        AuthUser user = (AuthUser) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo.setRoles(Sets.newSet(String.valueOf(roles.get(user.getRole()))));
        return authorizationInfo;
    }

    /**
     * 获取认证缓存key
     * @param principals    凭证
     * @return              认证缓存key,默认是登录名loginName
     */
    @Override
    protected Object getAuthenticationCacheKey(PrincipalCollection principals) {
        AuthUser authUser = (AuthUser) super.getAvailablePrincipal(principals);
        return authUser.toString();
    }

    /**
     * 获取授权缓存key
     * @param principals    凭证
     * @return              授权缓存key
     */
    @Override
    protected Object getAuthorizationCacheKey(PrincipalCollection principals) {
        AuthUser authUser = (AuthUser) super.getAvailablePrincipal(principals);
        return authUser.toString();
    }

    /**
     * 清除用户缓存
     * @param authUser      认证用户
     */
    public void removeUserCache(AuthUser authUser){
        SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
        principalCollection.add(authUser, super.getName());
        super.clearCache(principalCollection);
    }

    /**
     * 清除用户缓存
     * @param loginName     用户登录名
     */
    public void removeUserCache(String loginName){
        removeUserCache(new AuthUser(loginName));
    }
}
