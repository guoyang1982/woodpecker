package com.letv.woodpecker.wpwebapp.auth.shiro;

import com.letv.woodpecker.wpwebapp.utils.DigestUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.InitializingBean;

/**
 * shiro加密配置
 * @author meijunjie @date 2018/7/5
 */
@Getter
@Setter
public class PasswordHash implements InitializingBean {

    private String algorithmName;
    private int hashIterations;

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(algorithmName,"algorithmName must be MD5、SHA-1、SHA-256、SHA-384、SHA-512");
    }

    public String toHex(Object source, Object salt){
        return DigestUtils.hashByShiro(algorithmName, source, salt, hashIterations);
    }
}
