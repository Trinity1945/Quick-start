package com.zhangyh.management.admin.aspect;

import com.zhangyh.management.admin.annotation.AuthCheck;
import com.zhangyh.management.admin.annotation.PermissionEnum;
import com.zhangyh.management.admin.model.po.UserAccount;
import com.zhangyh.management.admin.service.UserAccountService;
import com.zhangyh.management.common.constants.ErrorCode;
import com.zhangyh.management.common.exception.BusinessException;
import com.zhangyh.management.common.util.RequestHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author zhangyh
 * @Date 2023/4/6 15:25
 * @desc 权限校验
 */
@Order(-1)
@Slf4j
@Aspect
@Component
public class AuthCheckAspect {
    @Resource
    private UserAccountService userService;

    @Pointcut("@annotation(com.zhangyh.management.admin.annotation.AuthCheck)")
    public void point() {}

    @Around("point()")
    public Object doAuthCheck(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = RequestHolder.getHttpServletRequest();
        PermissionEnum[] allowedPermissions = getAllowedPermissions(joinPoint.getTarget().getClass(), joinPoint.getSignature());
        if (!checkPermission(request, allowedPermissions)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return joinPoint.proceed(args);
    }

    /**
     * 获取接口需要的权限
     * @param clazz
     * @param signature
     * @return
     */
    private PermissionEnum[] getAllowedPermissions(Class<?> clazz, Signature signature) {
        AuthCheck classAuthCheck = clazz.getAnnotation(AuthCheck.class);
        AuthCheck methodAuthCheck = ((MethodSignature) signature).getMethod().getAnnotation(AuthCheck.class);
        if (methodAuthCheck != null && methodAuthCheck.value().length > 0) {
            return methodAuthCheck.value();
        } else if (classAuthCheck != null && classAuthCheck.value().length > 0) {
            return classAuthCheck.value();
        } else {
            return new PermissionEnum[]{};
        }
    }

    /**
     * 校验权限
     * @param request
     * @param allowedPermissions
     * @return
     */
    private boolean checkPermission(HttpServletRequest request, PermissionEnum[] allowedPermissions) {
        if (allowedPermissions.length == 0) {
            return true;
        }
        UserAccount currentUser = userService.getCurrentUser(request);
        String userPermissions = currentUser.getPermissions();
        String[] permission = userPermissions.split(",");

        for (PermissionEnum allowedPermission : allowedPermissions) {
            //通过code校验
            long count = Arrays.stream(permission).filter(p -> p.contains(String.valueOf(allowedPermission.getCode()))).count();
            if(count<=0){
                return false;
            }
        }
        return true;
    }

    @AfterThrowing(pointcut = "point()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {

       log.info("------------------权限校验异常----------------");
    }
}
