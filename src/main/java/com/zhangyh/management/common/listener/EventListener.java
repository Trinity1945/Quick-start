package com.zhangyh.management.common.listener;

import com.zhangyh.management.admin.model.po.SysLog;
import com.zhangyh.management.admin.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:42
 * @desc
 */
@Slf4j
@Component
public class EventListener {

    @Resource
    LogService logService;

    // 开启异步
    @Async
    // 开启监听
    @org.springframework.context.event.EventListener(SysLog.class)
    public void saveSysLog(SysLog event) {
        log.info("=====即将异步保存到数据库======");
        logService.save(event);
    }

}
