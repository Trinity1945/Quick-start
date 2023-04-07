package com.zhangyh.management.admin.model.po;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhangyh.management.common.util.sychronize.annotation.SqlColumn;
import com.zhangyh.management.common.util.sychronize.annotation.SqlSync;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangyh
 * @Date 2023/4/6 15:29
 * @desc
 */
@SqlSync(tableName = "user_account")
@TableName("user_account")
@Data
public class UserAccount implements Serializable {

    public static final String ID="id";
    public static final String USERNAME="username";
    public static final String PASSWORD="password";
    public static final String PERMISSIONS="permissions";
    public static final String DELETED="deleted";
    public static final String STATE="state";
    public static final String EXPIRED_TIME="expired_time";
    public static final String CREATE_TIME="create_time";
    public static final String UPDATE_TIME="update_time";


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @SqlColumn(field = "id",comment = "主键",index = SqlColumn.SqlIndex.PRI,autoIncrement = true,allowNull = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("username")
    @SqlColumn(field = "username",comment = "账号",allowNull = false,index = SqlColumn.SqlIndex.UNI)
    private String username;

    @SqlColumn(field = "password",comment = "密码",allowNull = false)
    @TableField("password")
    private String password;

    @SqlColumn(field = "permissions",comment = "用户权限 格式：1,2 数字对应枚举PermissionEnum的code")
    @TableField
    private String permissions;

    @SqlColumn(field = "deleted",defaultValue = "0",comment = "是否删除 0否 1是",length = 2)
    @TableField
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

    @SqlColumn(field = "state",defaultValue = "0",comment = "账号状态")
    @TableField
    private Integer state;

    @SqlColumn(field = "expired_time",comment = "账号过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("expired_time")
    private Date expiredTime;

    @SqlColumn(field = "create_time",comment = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;

    @SqlColumn(field = "update_time",comment = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    private Date updateTime;

}
