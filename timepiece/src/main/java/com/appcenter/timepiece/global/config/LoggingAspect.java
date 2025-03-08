package com.appcenter.timepiece.global.config;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.appcenter.timepiece.controller.*.*(..))")
    private void cut() {
    }

    @Around("cut()")
    public Object aroundLog(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        // 메서드 정보 받아오기
        Method method = getMethod(proceedingJoinPoint);
        log.info("======= method name = {} =======", method.getName());

        // 파라미터 받아오기
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length == 0) {
            log.info("no parameter");
        }
        for (Object arg : args) {
            if (arg != null) {
                log.info("parameter = {}, {}", arg.getClass().getSimpleName(), arg.toString());
            }
        }

        // proceed()를 호출하여 실제 메서드 실행
        Object returnObj = proceedingJoinPoint.proceed();

        // 메서드의 리턴값 로깅
        log.info("return value = {}", returnObj);

        return returnObj;
    }


    private Method getMethod(ProceedingJoinPoint proceedingJoinPoint) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        return signature.getMethod();
    }

}
