package org.huel.cloudhub.client.controller.common;

import org.huel.cloudhub.common.ErrorCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author RollW
 */
@RestController
@CommonApi
public class ErrorCodeMessageController {
    @GetMapping("/errorCode")
    public String errorCode(@RequestParam(name = "error") String errorCode) {
        return Objects.requireNonNull(
                ErrorCode.getErrorByValue(errorCode)).name();
    }
}
