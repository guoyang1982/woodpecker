package com.letv.woodpecker.wpwebapp.utils;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.commons.lang.math.JVMRandom;


/**
 * Common utils
 * @author meijunjie 2018
 */
public class Utils {

    public static final char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
            'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9' };

    public static final char[] nums = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private static JVMRandom random = new JVMRandom();

    /**
     * 深拷贝一个对象
     *
     * @param t 要拷贝的对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T t) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(t);
            ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray()));
            return (T) oi.readObject();
        } catch (Exception e) {
            throw new RuntimeException("没有实现序列化接口", e);
        }
    }

    public static String implode(List<?> dataList, String glue) {
        if (dataList == null || dataList.size() == 0) {
            return "";
        }

        StringBuffer strBuffer = new StringBuffer();
        for (Object obj : dataList) {
            if (obj == null) {
                strBuffer.append("");
            } else {
                strBuffer.append(obj.toString());
            }
            strBuffer.append(glue);
        }

        int len = strBuffer.length();

        return strBuffer.delete(len - glue.length(), len).toString();
    }

    public static String implode(List<?> dataList) {
        return implode(dataList, ",");
    }

    public static String implode(Object[] datas) {
        if (datas == null || datas.length == 0) {
            return "";
        }

        return Utils.implode(Arrays.asList(datas));
    }

    public static List<Object> asList(Object... params) {
        List<Object> list = new ArrayList<Object>();
        if (params != null) {
            for (Object p : params) {
                list.add(p);
            }
        }

        return list;
    }

    public static long phpTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    private static final Pattern mailPattern = Pattern
            .compile("^[a-zA-Z0-9_\\-\\.]+@[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-]+){0,2}\\.((com)|(cn)|(net))$");

    // Pattern.compile("^((13)|(15)|(18))\\d{9}$");
    private static final Pattern mobilePattern = Pattern.compile("^[1-9][0-9]{10,12}$");

    private static final Pattern numPattern = Pattern.compile("^-{0,1}\\d+\\.{0,1}\\d*$");

    private static final Pattern positiveIntNumPattern = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isEmail(String email) {
        return email != null && mailPattern.matcher(email).matches();
    }

    public static boolean isMobile(String mobile) {
        return mobile != null && mobilePattern.matcher(mobile).matches();
    }

    public static boolean isNum(String ch) {
        return ch != null && numPattern.matcher(ch.trim()).matches();
    }

    public static long ip2long(String ip) {
        String default_ip = "127.0.0.1";
        if (StringUtils.isEmpty(ip)) {
            ip = default_ip;
        }

        try {
            String[] splits = ip.trim().split("\\.");
            int segmentCount = splits.length;
            if (segmentCount == 4) {
                long ipnum = 0;
                for (int i = segmentCount - 1; i >= 0; i--) {
                    ipnum += (Long.parseLong(splits[i]) << ((segmentCount - 1 - i) * 8));
                }

                return ipnum;
            }
        } catch (Exception e) {
            ;
        }

        return Utils.ip2long(default_ip);
    }

    public static String ip2string(long ipnum) {
        StringBuffer ipBuffer = new StringBuffer();
        ipBuffer.append(String.valueOf((ipnum >>> 24))).append(".");
        ipBuffer.append(String.valueOf((ipnum & 0x00FFFFFF) >>> 16)).append(".");
        ipBuffer.append(String.valueOf((ipnum & 0x0000FFFF) >>> 8)).append(".");
        ipBuffer.append(String.valueOf((ipnum & 0x000000FF)));

        return ipBuffer.toString();
    }

    /**
     * 生成随机字符串
     *
     * 性能：生成100万次50个长度的随机字符串耗时不超过1500毫秒（1.5秒）
     *
     * @param length
     * @return String
     */
    public static String randString(int length) {
        return Utils.randString(length, false);
    }

    public static String randString(int length, boolean onlyDigital) {
        StringBuilder builder = new StringBuilder();
        if (onlyDigital) {
            int len = nums.length;
            for (int i = 0; i < length; i++) {
                builder.append(nums[random.nextInt(len)]);
            }
        } else {
            int len = chars.length;
            for (int i = 0; i < length; i++) {
                builder.append(chars[random.nextInt(len)]);
            }
        }

        return builder.toString();
    }

    public static String fill(String piece, int repeat) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            builder.append(piece);
        }

        return builder.toString();
    }

    public static String encode(long num) {
        long lshift = num << 2 | 3;
        return Base64.encodeBase64URLSafeString(String.valueOf(lshift).getBytes(Charsets.UTF_8));
    }

    public static Long decode(String str) {
        try {
            if (str == null || str.length() == 0) {
                return null;
            }

            String decode = new String(Base64.decodeBase64(str), Charsets.UTF_8);
            return Long.parseLong(decode) >> 2;
        } catch (Exception e) {
            return null;
        }
    }

    public static String encode64(String content) {
        return Base64.encodeBase64URLSafeString(content.getBytes(Charsets.UTF_8));
    }

    public static String decode64(String content) {
        return new String(Base64.decodeBase64(content), Charsets.UTF_8);
    }

    public static String encode64(BufferedImage image, boolean urlSafe) {
        if (image == null) {
            return null;
        }

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", out);
            return urlSafe ? Base64.encodeBase64URLSafeString(out.toByteArray())
                    : Base64.encodeBase64String(out.toByteArray());
        } catch (IOException e) {
            return null;
        }

    }

    public static boolean isPositiveIntNum(String ch) {
        boolean matches = (ch != null && positiveIntNumPattern.matcher(ch.trim()).matches());
        if (matches) {
            try {
                return Integer.parseInt(ch.trim()) <= Integer.MAX_VALUE;
            } catch (Exception e) {
                ;
            }
        }

        return false;
    }

    public static <T> List<T> splitToList(String str, String separator, Class<T> clazz) {
        List<T> retList = new ArrayList<T>();
        try {
            String[] split = str.split(separator);
            for (String s : split) {
                if (StringUtils.isNotBlank(s)) {
                    retList.add(clazz.getConstructor(String.class).newInstance(s.trim()));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return retList;
    }

    public static String like(String val, boolean isHeadTail) {
        if (StringUtils.isEmpty(val)) {
            return "";
        }
        return isHeadTail ? ("%" + val + "%") : (val + "%");
    }

}
