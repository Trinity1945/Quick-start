package com.zhangyh.management.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/7  21:59
 */
@Data
public class UserUpdateDto {

    @NotNull(message = "用户id不能为空")
    private Integer id;

    private String permissions;

    private Date expiredTime;

    private String name;

    private Integer gender;

    private String mobile;

    private String email;

    private Date birthday;
}
