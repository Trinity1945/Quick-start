package com.zhangyh.management.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author zhangyh
 * @Date 2023/4/7 17:19
 * @desc
 */
public class ThrowableUtil {
    /**
     * 获取堆栈信息
     */
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}
