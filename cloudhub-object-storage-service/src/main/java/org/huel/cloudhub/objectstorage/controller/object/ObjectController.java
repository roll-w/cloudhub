package org.huel.cloudhub.objectstorage.controller.object;

import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoDto;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.objectstorage.data.dto.object.ObjectRenameRequest;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.huel.cloudhub.objectstorage.event.object.ObjectDeleteRequestEvent;
import org.huel.cloudhub.objectstorage.service.bucket.BucketAuthService;
import org.huel.cloudhub.objectstorage.service.object.ObjectMetadataService;
import org.huel.cloudhub.objectstorage.service.object.ObjectRuntimeException;
import org.huel.cloudhub.objectstorage.service.object.ObjectService;
import org.huel.cloudhub.objectstorage.service.user.UserGetter;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.HttpResponseEntity;
import org.huel.cloudhub.web.UserErrorCode;
import org.huel.cloudhub.web.WebCommonErrorCode;
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
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE,
                    "Have no permission to read.");
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
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE,
                    "Have no permission to write.");
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
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE,
                    "Have no permission to delete.");
        }
        final String objectName = ObjectHelper.readPath(request);
        if (objectName.isEmpty()) {
            throw new ObjectRuntimeException(WebCommonErrorCode.ERROR_PARAM_FAILED,
                    "Invalid object name");
        }
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        applicationEventPublisher.publishEvent(
                new ObjectDeleteRequestEvent(objectInfo));

        objectService.deleteObject(objectInfo);
        return HttpResponseEntity.success();
    }

    @PostMapping("/setting/rename")
    public HttpResponseEntity<ObjectInfoVo> renameObject(
            HttpServletRequest request, @RequestBody ObjectRenameRequest objectRenameRequest) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        if (userInfo == null) {
            throw new ObjectRuntimeException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }
        BucketAuthService.BucketControlCode controlCode =
                bucketAuthService.allowWrite(userInfo, objectRenameRequest.bucketName());
        if (!controlCode.isSuccess()) {
            throw new ObjectRuntimeException(AuthErrorCode.ERROR_NOT_HAS_ROLE);
        }
        ObjectInfo objectInfo = new ObjectInfo(
                objectRenameRequest.objectName(),
                objectRenameRequest.bucketName());

        ObjectInfoDto res = objectService.renameObject(
                objectInfo, objectRenameRequest.newName());
        return HttpResponseEntity.success(ObjectInfoVo.from(res));
    }


}
