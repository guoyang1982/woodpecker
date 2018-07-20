package com.letv.woodpecker.wpwebapp.utils;

import org.apache.commons.codec.Charsets;
import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * Created by meijunjie on 2018/7/5.
 */
public class DigestUtils extends org.springframework.util.DigestUtils {

    public static String md5Hex(final String data){
        return DigestUtils.md5DigestAsHex(data.getBytes(Charsets.toCharset("UTF-8")));
    }

    public static String md5Hex(final byte[] bytes){
        return DigestUtils.md5DigestAsHex(bytes);
    }

    public static String hashByShiro(String algorithmName, Object source, Object salt, int hashIterations){
        return new SimpleHash(algorithmName, source, salt, hashIterations).toHex();
    }
}
