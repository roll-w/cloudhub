package org.huel.cloudhub.client.controller.bucket;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("/api/admin/bucket/")
public @interface BucketAdminApi {
}
