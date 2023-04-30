package org.huel.cloudhub.client.disk.domain.operatelog.context;

import org.huel.cloudhub.client.disk.domain.operatelog.BuiltinOperationType;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * @author RollW
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({METHOD})
public @interface BuiltinOperate {
    BuiltinOperationType value();
}
