package org.huel.cloudhub.objectstorage.controller.user;

import org.apache.commons.lang3.Validate;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.data.dto.user.UserRegisterRequest;
import org.huel.cloudhub.objectstorage.service.user.UserService;
import org.huel.cloudhub.web.MessagePackage;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RollW
 */
@UserApi
@RestController
public class UserRegisterController {
    static final String MISSING_PARAM = "Param %s cannot be empty.";
    private final UserService userService;

    public UserRegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public HttpResponseEntity<UserInfo> register(@RequestBody UserRegisterRequest registerRequest) {
        Validate.notEmpty(registerRequest.getUsername(), MISSING_PARAM, "username");
        Validate.notEmpty(registerRequest.getPassword(), MISSING_PARAM, "password");
        Validate.notEmpty(registerRequest.getEmail(), MISSING_PARAM, "email");

        MessagePackage<UserInfo> infoMessagePackage =
                userService.registerUser(
                        registerRequest.getUsername(),
                        registerRequest.getPassword(),
                        registerRequest.getEmail()
                );
        return HttpResponseEntity.of(
                infoMessagePackage.toResponseBody()
        );
    }

}