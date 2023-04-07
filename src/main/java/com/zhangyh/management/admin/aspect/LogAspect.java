package com.zhangyh.management.admin.aspect;

import com.zhangyh.management.admin.annotation.Log;
import com.zhangyh.management.admin.annotation.LogLevel;
import com.zhangyh.management.admin.model.po.SysLog;
import com.zhangyh.management.admin.model.po.UserAccount;
import com.zhangyh.management.admin.service.UserAccountService;
import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.exception.BusinessException;
import com.zhangyh.management.common.listener.EventPubListener;
import com.zhangyh.management.common.util.IpUtils;
import com.zhangyh.management.common.util.RequestHolder;
import com.zhangyh.management.common.util.ThrowableUtil;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

/**
 * @author zhangyh
 * @Date 2023/4/7 16:19
 * @desc
 */
@Order(2)
@Component
@Aspect
public class LogAspect {

    @Resource
    private EventPubListener eventPubListener;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

    @Resource
    UserAccountService userAccountService;
    private final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Pointcut("@annotation(com.zhangyh.management.admin.annotation.Log)")
    public void point() {
    }

    @Around("point()")
    public Object doAuthCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        currentTime.set(System.currentTimeMillis());
        Log log = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(Log.class);
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String ip = IpUtils.getIpAddr(request);
        SysLog sysLog = new SysLog();
        sysLog.setBusinessType(log.businessType().getCode());
        sysLog.setLevel(log.value().getCode());
        sysLog.setRequestMethod(method);
        sysLog.setOperIp(ip);
        sysLog.setOperUrl(url);
        sysLog.setCreateTime(new Date());
        UserAccount currentUser = userAccountService.getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNLOGIN);
        }
        sysLog.setOperName(currentUser.getUsername());
        logger.info("=======日志发送成功，内容：{}", sysLog);
        Object[] args = joinPoint.getArgs();
        // 执行原方法
        Object result = joinPoint.proceed(args);
        long totalTimeMillis = System.currentTimeMillis() - currentTime.get();
        currentTime.remove();
        sysLog.setOperTime(totalTimeMillis);
        // 发布消息 --异步保存
        eventPubListener.pushListener(sysLog);
        // 生成请求唯一 id
        String requestId = UUID.randomUUID().toString();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        logger.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                request.getRemoteHost(), reqParam);
        // 输出响应日志
        logger.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        return result;
    }

    @AfterThrowing(pointcut = "point()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = ((MethodSignature) joinPoint.getSignature()).getMethod()
                .getAnnotation(Log.class);
        SysLog sysLog = new SysLog();
        sysLog.setLevel(LogLevel.ERROR.getCode());
        sysLog.setBusinessType(log.businessType().getCode());
        sysLog.setOperTime(System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        UserAccount currentUser = userAccountService.getCurrentUser(request);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.UNLOGIN);
        }
        sysLog.setOperName(currentUser.getUsername());
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String ip = IpUtils.getIpAddr(request);
        sysLog.setOperUrl(url);
        sysLog.setOperIp(ip);
        sysLog.setRequestMethod(method);
        sysLog.setCreateTime(new Date());
        sysLog.setExceptionDetail(ThrowableUtil.getStackTrace(e));
        //异步保存
        eventPubListener.pushListener(sysLog);
    }
}
