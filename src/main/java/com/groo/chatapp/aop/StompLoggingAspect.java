package com.groo.chatapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class StompLoggingAspect {

    @Around("@annotation(org.springframework.messaging.handler.annotation.MessageMapping)")
    public Object logStompMessages(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("[ STOMP request ] {}", Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            log.info("[ STOMP response ] {}", result);
            return result;
        } catch (Throwable throwable) {
            log.error("[ STOMP failed ] {}", throwable.getMessage());
            throw throwable;
        }
    }
}
