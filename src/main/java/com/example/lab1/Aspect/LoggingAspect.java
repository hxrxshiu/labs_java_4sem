package com.example.lab1.Aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.lab1.Controller.*.*(..))")
    public void logMethodCall(JoinPoint jp) {
        log.info("Executing: {}.{}() with args: {}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                Arrays.toString(jp.getArgs()));
    }

    @AfterThrowing(
            pointcut = "execution(* com.example.lab1..*.*(..))",
            throwing = "ex"
    )
    public void logException(JoinPoint jp, Throwable ex) {
        log.error("Exception in {}.{}(): {}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                ex.getMessage(),
                ex);
    }
}