package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Mark the system resource access as requiring authentication.
 *
 * @author RollW
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({METHOD})
public @interface SystemResourceAuthenticate {
    /**
     * System resource kind. The default value is the first
     * element of {@link SystemResourceKind}.
     */
    SystemResourceKind kind() default SystemResourceKind.FILE;

    String kindParam() default "";

    boolean inferredKind() default true;

    long id() default INVALID_ID;

    String idParam() default "";

    /**
     * The action of the system resource. The default value
     * is the first element of {@link Action}.
     */
    Action action() default Action.CREATE;

    boolean inferredAction() default true;

    long INVALID_ID = -1000L;
}
