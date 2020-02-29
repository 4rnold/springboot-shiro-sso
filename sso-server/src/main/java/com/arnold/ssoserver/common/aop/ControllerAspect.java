package com.arnold.ssoserver.common.aop;

import com.arnold.ssoserver.common.entity.SSOResponse;
import com.arnold.ssoserver.common.exception.SSOException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerAspect {

    @Pointcut("execution(public com.arnold.ssoserver.common.entity.SSOResponse com.arnold.ssoserver.system.controller.*.*(..))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) {
        long startTime = System.currentTimeMillis();
        SSOResponse response = null;

        try {
            response = (SSOResponse) pjp.proceed();

            long elapsedTime = System.currentTimeMillis() - startTime;
            log.info("[{}]use time:{}",pjp.getSignature(), elapsedTime);
        } catch (Throwable throwable) {
            response = handlerException(pjp, throwable);
        }
        return response;
    }

    private SSOResponse handlerException(ProceedingJoinPoint pjp, Throwable e) {
        log.debug("exception",e);


        SSOResponse response = new SSOResponse();

        //认证错误
        if (e instanceof SSOException) {
            return new SSOResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage());
        } /*else if (e instanceof IncorrectCredentialsException) {
            return new FebsResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message("密码错误");
        } else if (e instanceof AuthenticationException) {
            return new FebsResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message(e.getMessage());
        } else if (e instanceof AuthorizationException) {
            return new FebsResponse().code(HttpStatus.UNAUTHORIZED).message(e.getMessage());
        }*/ else {
            return new SSOResponse().code(HttpStatus.INTERNAL_SERVER_ERROR).message("系统内部有错误");
        }
    }


}
