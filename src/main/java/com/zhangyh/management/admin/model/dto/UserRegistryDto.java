package com.zhangyh.management.admin.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * @author zhangyh
 * @Date 2023/4/7 11:41
 * @desc
 */
@Data
public class UserRegistryDto {

    //账号
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "账号只能为数字和英文字母大小写，且长度为 5~15")
    @NotBlank(message = "账号不能为空")
    private String username;

    //密码
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$", message = "密码只能为数字和英文字母大小写，且长度为 6~15")
    private String password;

    //重复密码
    @NotBlank(message = "请再次输入密码")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,15}$", message = "密码只能为数字和英文字母大小写，且长度为 6~15")
    private String checkPassword;

    //手机号
    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?1[3456789]\\d{9}$", message = "手机号码格式不正确")
    private String mobile;

    //性别
    private Integer gender;

    @Email
    private String email;

    //用户姓名
    private String name;

    //生日
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date birthday;

}
