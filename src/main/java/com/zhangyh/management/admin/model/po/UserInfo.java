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
 * @Date 2023/4/6 16:15
 * @desc
 */
@SqlSync(tableName = "user_info")
@TableName("user_info")
@Data
public class UserInfo implements Serializable {

    public static final String ID="id";
    public static final String NAME="name";
    public static final String ACCOUNT_ID="account_id";
    public static final String GENDER="gender";
    public static final String MOBILE="mobile";
    public static final String EMAIL="email";
    public static final String DELETED="deleted";
    public static final String BIRTHDAY="birthday";
    public static final String CREATE_TIME="create_time";
    public static final String UPDATE_TIME="update_time";

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @SqlColumn(field = "id",comment = "主键",index = SqlColumn.SqlIndex.PRI,autoIncrement = true,allowNull = false)
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField
    @SqlColumn(field = "name",comment = "用户名")
    private String name;

    @TableField("account_id")
    @SqlColumn(field = "account_id",comment = "账号ID 关联user_account表")
    private Integer accountId;

    @TableField
    @SqlColumn(field = "gender",comment = "性别 0男 1女",defaultValue = "0")
    private Integer gender;

    @TableField
    @SqlColumn(field = "mobile",comment = "手机号码")
    private String mobile;

    @TableField
    @SqlColumn(field = "email",comment = "邮箱")
    private String email;

    @TableField
    @TableLogic(value = "0", delval = "1")
    @SqlColumn(field = "deleted",defaultValue = "0",comment = "是否删除 0否 1是")
    private Integer deleted;

    @TableField
    @SqlColumn(field = "birthday",comment = "出生日期")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @SqlColumn(field = "create_time",comment = "更新时间")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @SqlColumn(field = "update_time",comment = "更新时间")
    private Date updateTime;

}
