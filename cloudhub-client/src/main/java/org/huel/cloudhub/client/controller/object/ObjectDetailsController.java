package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.client.data.dto.object.ObjectMetadataRemoveRequest;
import org.huel.cloudhub.client.data.dto.object.ObjectMetadataSetRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.bucket.BucketAuthService;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
@RestController
@ObjectApi
public class ObjectDetailsController {
    private final ObjectService objectService;
    private final ObjectMetadataService objectMetadataService;
    private final BucketAuthService bucketAuthService;
    private final UserGetter userGetter;
    private final Logger logger = LoggerFactory.getLogger(ObjectDetailsController.class);

    public ObjectDetailsController(ObjectService objectService,
                                   ObjectMetadataService objectMetadataService,
                                   BucketAuthService bucketAuthService,
                                   UserGetter userGetter) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.bucketAuthService = bucketAuthService;
        this.userGetter = userGetter;
    }

    @GetMapping("/get")
    public HttpResponseEntity<List<ObjectInfoVo>> getObjectsInBucket(
            HttpServletRequest request,
            @RequestParam String bucketName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        if (!bucketAuthService.isOwnerOf(userInfo, bucketName)) {
            return HttpResponseEntity.failure("Not permitted.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        List<ObjectInfoVo> vos = objectService.getObjectsInBucket(bucketName)
                .stream().map(ObjectInfoVo::from).toList();
        return HttpResponseEntity.success(vos);
    }

    @GetMapping("/metadata/get")
    public HttpResponseEntity<Map<String, String>> getObjectMetadata(HttpServletRequest request,
                                                                     String bucketName,
                                                                     String objectName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode controlCode =
                bucketAuthService.allowRead(userInfo, bucketName);
        if (!controlCode.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to read.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        Map<String, String> metadata = objectMetadataService
                .getObjectMetadata(bucketName, objectName);
        if (metadata == null) {
            return HttpResponseEntity.failure("Not found",
                    ErrorCode.ERROR_DATA_NOT_EXIST);
        }

        return HttpResponseEntity.success(metadata);
    }

    @PostMapping("/metadata/set")
    public HttpResponseEntity<Void> setObjectMetadata(HttpServletRequest request,
                                                      @RequestBody ObjectMetadataSetRequest setRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode controlCode =
                bucketAuthService.allowWrite(userInfo, setRequest.bucketName());
        if (!controlCode.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to read.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        var res = objectMetadataService.addObjectMetadataWithCheck(
                setRequest.bucketName(),
                setRequest.objectName(),
                setRequest.metadata()
        );
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @PostMapping("/metadata/remove")
    public HttpResponseEntity<Void> removeObjectMetadata(HttpServletRequest request,
                                                         @RequestBody ObjectMetadataRemoveRequest removeRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode controlCode =
                bucketAuthService.allowWrite(userInfo, removeRequest.bucketName());
        if (!controlCode.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to read.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        var res = objectMetadataService.removeObjectMetadata(
                removeRequest.bucketName(),
                removeRequest.objectName(),
                removeRequest.removeKeys()
        );
        return HttpResponseEntity.create(res.toResponseBody());
    }
}
