package org.huel.cloudhub.client.controller.server;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("/api/admin/status/")
public @interface ServerApi {
}
