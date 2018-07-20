package com.letv.woodpecker.wpserver.utils;

import java.security.MessageDigest;

/**
 * Created by zhusheng on 17/3/27.
 */
public class Md5Util {

    private static MessageDigest md5 = null;
    static
    {
        try
        {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 获取String的md5值
     * @param str
     * @return
     */
    public static String getMd5(String str)
    {
        byte[] bs = md5.digest(str.getBytes());
        StringBuilder sb = new StringBuilder(40);
        for(byte x:bs)
        {
            if((x & 0xff)>>4 == 0)
            {
                sb.append("0").append(Integer.toHexString(x & 0xff));
            } else
            {
                sb.append(Integer.toHexString(x & 0xff));
            }
        }
        return sb.toString();
    }


}