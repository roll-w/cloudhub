package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.client.data.dto.object.ObjectRenameRequest;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author RollW
 */
@RestController
@ObjectAdminApi
public class ObjectAdminManageController {
    private final Logger logger = LoggerFactory.getLogger(ObjectAdminManageController.class);

    private final ObjectService objectService;
    private final ObjectMetadataService objectMetadataService;
    private final UserGetter userGetter;


    public ObjectAdminManageController(ObjectService objectService,
                                       ObjectMetadataService objectMetadataService,
                                       UserGetter userGetter) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.userGetter = userGetter;
    }

    @GetMapping(value = "/v1/{bucketName}/**")
    public HttpResponseEntity<String> getObjectFile(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    @PathVariable("bucketName") String bucketName) throws IOException {
        var validateMessage =
                ValidateHelper.validateUserAdmin(request, userGetter);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody(d -> null));
        }
        String objectName = ObjectHelper.readPath(request);
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        response.setHeader("Content-Type", "application/octet-stream");
        Map<String, String> metadata = objectMetadataService
                .getObjectMetadata(bucketName, objectName);
        metadata.forEach(response::setHeader);
        objectService.getObjectData(objectInfo, response.getOutputStream());
        return null;
    }

    @PutMapping(value = "/v1/{bucketName}/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResponseEntity<ObjectInfoVo> uploadObject(HttpServletRequest request,
                                                         @PathVariable("bucketName") String bucketName,
                                                         @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        var validateMessage =
                ValidateHelper.validateUserAdmin(request, userGetter);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody(d -> null));
        }
        String objectName = ObjectHelper.readPath(request);
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        Map<String, String> metadata = ObjectHelper.buildInitialMetadata(objectFile);
        var res =
                objectService.saveObject(objectInfo, objectFile.getInputStream());
        if (res.errorCode().getState()) {
            objectMetadataService.addObjectMetadata(bucketName, objectName, metadata);
        }
        return HttpResponseEntity.create(
                res.toResponseBody(ObjectInfoVo::from));
    }

    @DeleteMapping(value = "/v1/{bucketName}/**")
    public HttpResponseEntity<Void> deleteObject(HttpServletRequest request,
                                                 @PathVariable("bucketName") String bucketName) {
        var validateMessage =
                ValidateHelper.validateUserAdmin(request, userGetter);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody(d -> null));
        }

        final String objectName = ObjectHelper.readPath(request);
        if (objectName.isEmpty()) {
            return HttpResponseEntity.failure("Not valid object name.",
                    ErrorCode.ERROR_NULL);
        }
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        var res =
                objectService.deleteObject(objectInfo);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    @PostMapping("/setting/rename")
    public HttpResponseEntity<ObjectInfoVo> renameObject(
            HttpServletRequest request, @RequestBody ObjectRenameRequest objectRenameRequest) {
        var validateMessage =
                ValidateHelper.validateUserAdmin(request, userGetter);
        if (validateMessage != null) {
            return HttpResponseEntity.create(
                    validateMessage.toResponseBody(d -> null));
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
