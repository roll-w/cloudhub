package org.huel.cloudhub.client.controller.user;

import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.annotation.*;

/**
 * @author RollW
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RequestMapping("/api/user/")
public @interface UserApi {
    //定义注解里面是空的有啥作用
}
