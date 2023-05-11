package org.huel.cloudhub.client.disk.controller;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.web.RequestMetadata;
import org.huel.cloudhub.web.RequestUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
@ControllerAdvice
public class RequestEnhanceControllerAdvice {

    @ModelAttribute
    public RequestMetadata requestMetadata(HttpServletRequest request) {
        ApiContextHolder.ApiContext apiContext = ApiContextHolder.getContext();
        if (apiContext != null && apiContext.ip() != null) {
            return new RequestMetadata(
                    apiContext.ip(),
                    request.getHeader("User-Agent"),
                    apiContext.timestamp()
            );
        }
        return new RequestMetadata(
                RequestUtils.getRemoteIpAddress(request),
                request.getHeader("User-Agent"),
                System.currentTimeMillis()
        );
    }
}
