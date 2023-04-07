package com.zhangyh.management.admin.annotation;

import lombok.Getter;

/**
 * @author zhangyh
 * @Date 2023/4/6 14:20
 * @desc 权限信息
 */
@Getter
public enum PermissionEnum {
    USER(0,"用户权限"),
    ADMIN(1,"管理员权限")
    ;

    PermissionEnum( Integer code,String name) {
        this.name = name;
        this.code = code;
    }

    /**
     * 权限名
     */
    private  String name;

    /**
     * 权限编号
     */
    private  Integer code;
}
