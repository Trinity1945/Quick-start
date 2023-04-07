package com.zhangyh.management.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhangyh.management.admin.mapper.LogMapper;
import com.zhangyh.management.admin.model.po.SysLog;
import com.zhangyh.management.admin.service.LogService;
import org.springframework.stereotype.Service;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:44
 * @desc
 */
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, SysLog> implements LogService {
}
