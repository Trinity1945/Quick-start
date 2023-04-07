package com.zhangyh.management.admin.model.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zhangyh.management.common.util.sychronize.annotation.SqlColumn;
import com.zhangyh.management.common.util.sychronize.annotation.SqlSync;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:30
 * @desc
 */
@SqlSync(tableName = "sys_log")
@Data
@TableName("sys_log")
public class SysLog  implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String ID="id";
    public static final String TITLE="level";
    public static final String BUSINESS_TYPE="business_type";
    public static final String REQUEST_METHOD="request_method";
    public static final String OPER_NAME="oper_name";
    public static final String OPER_URL="oper_url";
    public static final String OPER_IP="oper_ip";
    public static final String OPER_TIME="oper_time";
    public static final String EXCEPTION_DETAIL="exception_detail";
    public static final String DELETED="deleted";
    public static final String CREATE_TIME="create_time";
    public static final String UPDATE_TIME="update_time";

    /**
     * 日志主键
     */
    @SqlColumn(field = "id",index = SqlColumn.SqlIndex.PRI,allowNull = false,autoIncrement = true,comment = "主键")
    @TableId
    private Integer id;

    /**
     * 操作模块
     */
    @SqlColumn(field = "level",comment = "日志级别")
    @TableField
    private Integer level;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    @SqlColumn(field = "business_type",comment = "业务类型")
    @TableField("business_type")
    private Integer businessType;

    /**
     * 请求方式
     */
    @SqlColumn(field = "request_method",comment = "请求方式")
    @TableField("request_method")
    private String requestMethod;

    /**
     * 操作人员
     */
    @SqlColumn(field = "oper_name",comment = "操作用户")
    @TableField("oper_name")
    private String operName;

    /**
     * 请求url
     */
    @SqlColumn(field = "oper_url",comment = "请求URL")
    @TableField("oper_url")
    private String operUrl;

    /**
     * 操作地址
     */
    @SqlColumn(field = "oper_ip",comment = "请求的用户IP")
    @TableField("oper_ip")
    private String operIp;

    /**
     * 操作时间
     */
    @SqlColumn(field = "oper_time",comment = "操作耗时")
    @TableField("oper_time")
    private Long operTime;

    @SqlColumn(field = "exception_detail",comment = "异常详情",length = 1024)
    @TableField("exception_detail")
    private String exceptionDetail;

    @SqlColumn(field = "deleted",defaultValue = "0",comment = "是否删除 0否 1是",length = 2)
    @TableField
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;

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
