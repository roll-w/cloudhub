package org.huel.cloudhub.web.controller.auth;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping(value = "/api/auth/", produces = MediaType.APPLICATION_JSON_VALUE)
public @interface AuthApi {
}
