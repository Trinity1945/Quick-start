package com.zhangyh.management.admin.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangyh
 * @Date 2023/4/7 14:03
 * @desc
 */
@EqualsAndHashCode
@Data
public class UserQueryDto extends PageRequest implements Serializable {

    private static final long serialVersionUID = 543654641L;

    private Integer id;

    private String username;

    private String name;

    private String permissions;

    private String gender;

    private Integer state;

    private Integer deleted;

    private Date birthday;

    private String mobile;

    private String email;
}
