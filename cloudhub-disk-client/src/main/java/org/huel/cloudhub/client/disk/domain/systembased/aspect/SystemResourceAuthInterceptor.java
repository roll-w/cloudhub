package org.huel.cloudhub.client.disk.domain.systembased.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.context.BuiltinOperate;
import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.client.disk.domain.user.UserIdentity;
import org.huel.cloudhub.web.AuthErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author RollW
 */
@Aspect
@Component
public class SystemResourceAuthInterceptor {
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public SystemResourceAuthInterceptor(SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }


    @Before("@annotation(org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticate)")
    public void beforeAuthentication(@NonNull JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        SystemResourceAuthenticate systemResourceAuthenticate =
                method.getAnnotation(SystemResourceAuthenticate.class);
        if (systemResourceAuthenticate == null) {
            return;
        }
        BuiltinOperate builtinOperate = method.getAnnotation(BuiltinOperate.class);
        Action action = findAction(systemResourceAuthenticate, builtinOperate);
        SystemResourceKind systemResourceKind = findResourceKind(joinPoint,
                method, systemResourceAuthenticate, builtinOperate);
        long resourceId = findResourceId(joinPoint, method, systemResourceAuthenticate);

        SystemResourceAuthenticationProvider authenticationProvider = systemResourceAuthenticationProviderFactory
                .getSystemResourceAuthenticationProvider(systemResourceKind);
        SystemResource systemResource = new SimpleSystemResource(resourceId, systemResourceKind);
        SystemAuthentication systemAuthentication = authenticationProvider.authenticate(
                systemResource,
                userIdentity, action
        );

        systemAuthentication.throwAuthenticationException();
    }

    private Action findAction(SystemResourceAuthenticate systemResourceAuthenticate,
                              BuiltinOperate builtinOperate) {
        if (!systemResourceAuthenticate.inferredAction()) {
            return systemResourceAuthenticate.action();
        }
        return checkNull(builtinOperate).value().getAction();
    }

    private SystemResourceKind findResourceKind(JoinPoint joinPoint,
                                                Method method,
                                                SystemResourceAuthenticate systemResourceAuthenticate,
                                                BuiltinOperate builtinOperate) {
        if (!systemResourceAuthenticate.inferredKind() &&
                systemResourceAuthenticate.kindParam().isEmpty()) {
            return checkKind(systemResourceAuthenticate.kind());
        }
        if (systemResourceAuthenticate.inferredKind()) {
            return checkKind(
                    checkNull(builtinOperate).value().getSystemResourceKind()
            );
        }
        Object obj = findParamValueOf(joinPoint, method, systemResourceAuthenticate.kindParam());
        if (obj instanceof SystemResourceKind kind) {
            return checkKind(kind);
        }
        throw new IllegalArgumentException("Cannot cast param value to SystemResourceKind: " + obj.getClass() +
                ", parameter name: " + systemResourceAuthenticate.kindParam() + ", in " + method);
    }

    private SystemResourceKind checkKind(SystemResourceKind kind) {
        if (kind == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH);
        }
        return kind;
    }

    private long findResourceId(JoinPoint joinPoint,
                                Method method,
                                SystemResourceAuthenticate systemResourceAuthenticate) {
        if (systemResourceAuthenticate.idParam().isEmpty()) {
            if (systemResourceAuthenticate.id() == SystemResourceAuthenticate.INVALID_ID) {
                throw new IllegalArgumentException("resourceId and resourceIdParam cannot be both empty");
            }
            return systemResourceAuthenticate.id();
        }
        Object obj = findParamValueOf(joinPoint, method, systemResourceAuthenticate.idParam());
        if (obj instanceof Long id) {
            return id;
        }
        if (obj instanceof String id) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException ignored) {
            }
        }
        throw new IllegalArgumentException("Cannot cast param value to long: " + obj.getClass() +
                ", parameter name: " + systemResourceAuthenticate.idParam() + ", in " + method);
    }

    private BuiltinOperate checkNull(BuiltinOperate builtinOperate) {
        if (builtinOperate == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH);
        }
        return builtinOperate;
    }

    private Object findParamValueOf(JoinPoint joinPoint,
                                    Method method, String name) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(name)) {
                return args[i];
            }
        }
        throw new IllegalArgumentException("No such param found in system resource authentication: " + name);
    }

}
