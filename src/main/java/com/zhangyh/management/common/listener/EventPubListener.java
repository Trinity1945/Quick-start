package com.zhangyh.management.common.listener;

import com.zhangyh.management.admin.model.po.SysLog;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:41
 * @desc
 */
@Component
public class EventPubListener {
    @Resource
    private ApplicationContext applicationContext;

    // 事件发布方法
    public void pushListener(SysLog sysLogEvent) {
        applicationContext.publishEvent(sysLogEvent);
    }
}
