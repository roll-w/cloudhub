package org.huel.cloudhub.objectstorage.controller.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectRevertRequest;
import org.huel.cloudhub.objectstorage.data.dto.object.VersionedObjectVo;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.data.entity.object.VersionedObject;
import org.huel.cloudhub.objectstorage.service.bucket.BucketAuthService;
import org.huel.cloudhub.objectstorage.service.object.ObjectErrorCode;
import org.huel.cloudhub.objectstorage.service.object.ObjectMetadataService;
import org.huel.cloudhub.objectstorage.service.object.ObjectRuntimeException;
import org.huel.cloudhub.objectstorage.service.object.ObjectService;
import org.huel.cloudhub.objectstorage.service.object.VersionedObjectService;
import org.huel.cloudhub.objectstorage.service.user.UserGetter;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
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
@ObjectVersionApi
public class ObjectVersionControlController {

    private final BucketAuthService bucketAuthService;
    private final UserGetter userGetter;
    private final ObjectService objectService;
    private final ObjectMetadataService objectMetadataService;
    private final VersionedObjectService versionedObjectService;

    public ObjectVersionControlController(BucketAuthService bucketAuthService,
                                          UserGetter userGetter,
                                          ObjectService objectService,
                                          ObjectMetadataService objectMetadataService,
                                          VersionedObjectService versionedObjectService) {
        this.bucketAuthService = bucketAuthService;
        this.userGetter = userGetter;
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.versionedObjectService = versionedObjectService;
    }

    @GetMapping("/get")
    public HttpResponseEntity<List<VersionedObjectVo>> getObjectVersions(
            HttpServletRequest request,
            @RequestParam String bucketName,
            @RequestParam String objectName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (!bucketAuthService.isOwnerOf(userInfo, bucketName)) {
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
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
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (!bucketAuthService.isOwnerOf(userInfo, revertRequest.bucketName())) {
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
        VersionedObject versionedObject = versionedObjectService.getObjectVersionOf(
                revertRequest.bucketName(),
                revertRequest.objectName(),
                revertRequest.version());
        if (versionedObject == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_VERSIONED_OBJECT_NOT_EXIST);
        }
        objectService.setObjectFileId(
                revertRequest.bucketName(),
                revertRequest.objectName(),
                versionedObject.getFileId());
        return HttpResponseEntity.success();
    }
}
