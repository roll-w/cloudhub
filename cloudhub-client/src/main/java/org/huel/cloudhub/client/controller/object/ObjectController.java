package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.client.data.dto.object.ObjectRenameRequest;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.event.object.ObjectDeleteRequestEvent;
import org.huel.cloudhub.client.service.bucket.BucketAuthService;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RollW
 */
@RestController
@ObjectApi
public class ObjectController {
    // Object: GET, PUT, DELETE
    // 使用不同的Http method区分操作
    private final Logger logger = LoggerFactory.getLogger(ObjectController.class);

    private final ObjectService objectService;
    private final ObjectMetadataService objectMetadataService;
    private final BucketAuthService bucketAuthService;
    private final UserGetter userGetter;
    private final ApplicationEventPublisher applicationEventPublisher;


    public ObjectController(ObjectService objectService,
                            ObjectMetadataService objectMetadataService,
                            BucketAuthService bucketAuthService,
                            UserGetter userGetter,
                            ApplicationEventPublisher applicationEventPublisher) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.bucketAuthService = bucketAuthService;
        this.userGetter = userGetter;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @GetMapping(value = "/v1/{bucketName}/**")
    public HttpResponseEntity<String> getObjectFile(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    @PathVariable("bucketName") String bucketName) throws IOException {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode allowRead = bucketAuthService.allowRead(userInfo, bucketName);
        if (!allowRead.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to read.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        String objectName = ObjectHelper.readPath(request);
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        return ObjectHelper.processGetObject(
                request, response, bucketName,
                objectName, objectInfo, objectMetadataService,
                applicationEventPublisher, objectService);
    }

    @PutMapping(value = "/v1/{bucketName}/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResponseEntity<ObjectInfoVo> uploadObject(HttpServletRequest request,
                                                         @PathVariable("bucketName") String bucketName,
                                                         @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode allowWrite = bucketAuthService.allowWrite(userInfo, bucketName);
        if (!allowWrite.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to write.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        return ObjectHelper.processObjectUpload(request, bucketName,
                objectFile, objectService, objectMetadataService,
                applicationEventPublisher);
    }

    @DeleteMapping(value = "/v1/{bucketName}/**")
    public HttpResponseEntity<Void> deleteObject(HttpServletRequest request,
                                                 @PathVariable("bucketName") String bucketName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketAuthService.BucketControlCode allowWrite = bucketAuthService.allowWrite(userInfo, bucketName);
        if (!allowWrite.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to delete.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        final String objectName = ObjectHelper.readPath(request);
        if (objectName.isEmpty()) {
            return HttpResponseEntity.failure("Not valid object name.",
                    ErrorCode.ERROR_NULL);
        }
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        applicationEventPublisher.publishEvent(
                new ObjectDeleteRequestEvent(objectInfo));
        var res =
                objectService.deleteObject(objectInfo);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @PostMapping("/setting/rename")
    public HttpResponseEntity<ObjectInfoVo> renameObject(
            HttpServletRequest request, @RequestBody ObjectRenameRequest objectRenameRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            return HttpResponseEntity.failure("User not login.",
                    ErrorCode.ERROR_USER_NOT_LOGIN);
        }
        BucketAuthService.BucketControlCode controlCode =
                bucketAuthService.allowWrite(userInfo, objectRenameRequest.bucketName());
        if (!controlCode.isSuccess()) {
            return HttpResponseEntity.failure("Has no permission.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        ObjectInfo objectInfo = new ObjectInfo(
                objectRenameRequest.objectName(),
                objectRenameRequest.bucketName());

        var res = objectService.renameObject(
                objectInfo, objectRenameRequest.newName());
        return HttpResponseEntity.create(
                res.toResponseBody(ObjectInfoVo::from));
    }


}
