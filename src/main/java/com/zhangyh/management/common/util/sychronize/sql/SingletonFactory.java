package com.zhangyh.management.common.util.sychronize.sql;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhangyh
 * @Date 2023/3/24 14:13
 * @desc 单例工厂
 */
public class SingletonFactory {

    private static final Map<String, Object> OBJECT_POOL = new ConcurrentHashMap<>();

    public static  <T> T getInstance(Class<T> c) {
        if (c == null) {
            throw new RuntimeException("参数缺失");
        }
        String key = c.toString();
        if (OBJECT_POOL.containsKey(key)) {
            return c.cast(OBJECT_POOL.get(key));
        } else {
            return c.cast(OBJECT_POOL.computeIfAbsent(key, k -> {
                try {
                    return c.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException();
                }
            }));
        }
    }
}
