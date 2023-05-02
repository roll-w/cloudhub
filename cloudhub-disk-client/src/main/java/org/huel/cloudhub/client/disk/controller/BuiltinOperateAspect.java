package org.huel.cloudhub.client.disk.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author RollW
 */
@Aspect
@Component
public class BuiltinOperateAspect {
    private static final Logger logger = LoggerFactory.getLogger(BuiltinOperateAspect.class);

    @Before("@annotation(org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate)")
    public void recordOperateType(JoinPoint joinPoint) {
        logger.info("We are in recordOperateType method.");
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        BuiltinOperate builtinOperate = method.getAnnotation(BuiltinOperate.class);
        logger.info("builtinOperate: {}", builtinOperate);
        if (builtinOperate == null) {
            return;
        }
        OperationContextHolder.getContext()
                .setOperateType(builtinOperate.value());

    }

}
