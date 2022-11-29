package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.object.ObjectRevertRequest;
import org.huel.cloudhub.client.data.dto.object.VersionedObjectVo;
import org.huel.cloudhub.client.data.entity.object.VersionedObject;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.object.VersionedObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author RollW
 */
@RestController
@ObjectAdminVersionApi
public class ObjectAdminVersionControlController {

    private final UserGetter userGetter;
    private final ObjectService objectService;
    private final VersionedObjectService versionedObjectService;

    public ObjectAdminVersionControlController(UserGetter userGetter,
                                               ObjectService objectService,
                                               VersionedObjectService versionedObjectService) {
        this.userGetter = userGetter;
        this.objectService = objectService;
        this.versionedObjectService = versionedObjectService;
    }

    @GetMapping("/get")
    public HttpResponseEntity<List<VersionedObjectVo>> getObjectVersions(
            HttpServletRequest request,
            @RequestParam String bucketName,
            @RequestParam String objectName) {
        var res = ValidateHelper.validateUserAdmin(request, userGetter);
        if (res != null) {
            return HttpResponseEntity.create(res.toResponseBody(data -> null));
        }
        List<VersionedObjectVo> versionedObjects = versionedObjectService
                .getObjectVersions(bucketName, objectName)
                .stream()
                .map(VersionedObjectVo::from)
                .toList();
        return HttpResponseEntity.success(versionedObjects);
    }

    @PostMapping("/revert")
    public HttpResponseEntity<Void> revertVersion(HttpServletRequest request,
                                                  @RequestBody ObjectRevertRequest revertRequest) {
        var validate = ValidateHelper.validateUserAdmin(request, userGetter);
        if (validate != null) {
            return HttpResponseEntity.create(validate.toResponseBody(data -> null));
        }
        VersionedObject versionedObject = versionedObjectService.getObjectVersionOf(
                revertRequest.bucketName(),
                revertRequest.objectName(),
                revertRequest.version());
        if (versionedObject == null) {
            return HttpResponseEntity.failure("Not exist version.",
                    ErrorCode.ERROR_DATA_NOT_EXIST);
        }
        var res = objectService.setObjectFileId(
                revertRequest.bucketName(),
                revertRequest.objectName(),
                versionedObject.getFileId());
        return HttpResponseEntity.create(res.toResponseBody());
    }
}
