package cn.finetool.common.util;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;

/**
 * 字符串封装工具类
 */
public class Strings {
    
    private Strings() {
        
    }

    public static <T> boolean equals(T obj1, T obj2) {
        if (obj1 == obj2) {
            return true;
        } else {
            return obj1 != null ? obj1.equals(obj2) : false;
        }
    }

    public static boolean isBlank(String str) {
        return StringUtils.isBlank(str);
    }

    public static boolean isNotBlank(String str) {
        return StringUtils.isNotBlank(str);
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection c) {
        return !isEmpty(c);
    }

    public static boolean isTrue(Boolean b) {
        return Boolean.TRUE.equals(b);
    }
}
