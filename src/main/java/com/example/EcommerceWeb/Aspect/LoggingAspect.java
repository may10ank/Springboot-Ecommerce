package com.example.EcommerceWeb.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
@Component
public class LoggingAspect {
    private final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.example.EcommerceWeb.*.*(..))")
    public void logMethodCall(JoinPoint jp) {
        log.info("Method Called "+jp.getSignature().getName());
    }
    @After("execution(* com.example.EcommerceWeb.*.*(..))")
    public void logMethodExecuted(JoinPoint jp) {
        log.info("Method Executed "+jp.getSignature().getName());
    }
    @AfterThrowing(pointcut = "execution(* com.example.EcommerceWeb.*.*(..))",throwing = "error")
    public void logMethodCrashed(JoinPoint jp,Throwable error) {
        log.error("Method "+jp.getSignature().getName()+"failed to execute because "+error.getMessage());
    }
    @AfterReturning("execution(* com.example.EcommerceWeb.*.*(..))")
    public void logMethodExecutedSuccess(JoinPoint jp) {
        log.info("Method Executed Successfully "+jp.getSignature().getName());
    }
}
