package org.huel.cloudhub.objectstorage.controller.server;

import org.huel.cloudhub.objectstorage.controller.ValidateHelper;
import org.huel.cloudhub.objectstorage.event.object.ObjectRequestCounter;
import org.huel.cloudhub.objectstorage.service.user.UserGetter;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author RollW
 */
@RestController
@StatApi
public class StatController {
    private final ObjectRequestCounter objectRequestCounter;
    private final UserGetter userGetter;

    public StatController(ObjectRequestCounter objectRequestCounter,
                          UserGetter userGetter) {
        this.objectRequestCounter = objectRequestCounter;
        this.userGetter = userGetter;
    }

    @GetMapping("/request/get")
    public HttpResponseEntity<Long> getGetRequestCount(HttpServletRequest request) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        return HttpResponseEntity.success(objectRequestCounter.getGetCount());
    }

}
