package org.huel.cloudhub.objectstorage.controller.object;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("/api/admin/object/")
public @interface ObjectAdminApi {
}
