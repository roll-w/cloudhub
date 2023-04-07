package org.huel.cloudhub.objectstorage.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("")
public @interface PublicUrlApi {
}
