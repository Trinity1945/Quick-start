package com.zhangyh.management.admin.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/4/6 14:18
 * @desc 权限校验
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface AuthCheck {

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("value")
    PermissionEnum[] name() default {};

    /**
     * 权限数组，来自PermissionEnum
     */
    @AliasFor("name")
    PermissionEnum[] value() default {};
}
