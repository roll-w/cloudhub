package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.data.dto.object.ObjectRevertRequest;
import org.huel.cloudhub.client.data.dto.object.VersionedObjectVo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.bucket.BucketAuthService;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
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
        if (!bucketAuthService.isOwnerOf(userInfo, bucketName)){
            return HttpResponseEntity.failure("No Permission.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
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
        if (!bucketAuthService.isOwnerOf(userInfo, revertRequest.bucketName())){
            return HttpResponseEntity.failure("No Permission.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        return null;
    }
}
