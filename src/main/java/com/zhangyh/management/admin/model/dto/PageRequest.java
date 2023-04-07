package com.zhangyh.management.admin.model.dto;

import com.zhangyh.management.common.constants.GlobalConstants;
import lombok.Data;

/**
 * @author zhangyh
 * @Date 2023/4/7 13:58
 * @desc 分页请求
 */
@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private final Long current = 1L;

    /**
     * 页面大小
     */
    private final Long pageSize = 10L;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private final String sortOrder = GlobalConstants.SORT_ORDER_ASC;
}
