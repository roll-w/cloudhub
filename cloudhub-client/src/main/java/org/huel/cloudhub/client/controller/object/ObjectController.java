package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.data.dto.object.ObjectInfo;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.bucket.BucketService;
import org.huel.cloudhub.client.service.object.ObjectMetadataHeaders;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final BucketService bucketService;
    private final UserGetter userGetter;


    public ObjectController(ObjectService objectService,
                            ObjectMetadataService objectMetadataService,
                            BucketService bucketService,
                            UserGetter userGetter) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.bucketService = bucketService;
        this.userGetter = userGetter;
        this.antPathMatcher = new AntPathMatcher();
    }

    @GetMapping(value = "/{bucketName}/**")
    public HttpResponseEntity<String> getObjectFile(HttpServletRequest request,
                                                    HttpServletResponse response,
                                                    @PathVariable("bucketName") String bucketName) throws IOException {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketService.BucketControlCode allowRead = bucketService.allowRead(userInfo, bucketName);
        if (!allowRead.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to read.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        String objectName = readPath(request);
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        response.setHeader("Content-Type", "application/octet-stream");
        Map<String, String> metadata = objectMetadataService
                .getObjectMetadata(bucketName, objectName);
        metadata.forEach(response::setHeader);
        objectService.getObjectData(objectInfo, response.getOutputStream());
        return null;
    }

    @PutMapping(value = "/{bucketName}/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResponseEntity<ObjectInfoVo> uploadObject(HttpServletRequest request,
                                                         @PathVariable("bucketName") String bucketName,
                                                         @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketService.BucketControlCode allowWrite = bucketService.allowWrite(userInfo, bucketName);
        if (!allowWrite.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to write.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        Map<String, String> metadata = buildInitialMetadata(objectFile);

        String objectName = readPath(request);
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        var res =
                objectService.saveObject(objectInfo, objectFile.getInputStream());
        if (res.errorCode().getState()) {
            objectMetadataService.addObjectMetadata(
                    bucketName, objectName, metadata);
        }
        return HttpResponseEntity.create(
                res.toResponseBody(ObjectInfoVo::from));
    }

    @DeleteMapping(value = "/{bucketName}/**")
    public HttpResponseEntity<Void> deleteObject(HttpServletRequest request,
                                                 @PathVariable("bucketName") String bucketName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        BucketService.BucketControlCode allowWrite = bucketService.allowWrite(userInfo, bucketName);
        if (!allowWrite.isSuccess()) {
            return HttpResponseEntity.failure("Have no permission to delete.",
                    ErrorCode.ERROR_PERMISSION_NOT_ALLOWED);
        }
        final String objectName = readPath(request);
        if (objectName.isEmpty()) {
            return HttpResponseEntity.failure("Not valid object name.",
                    ErrorCode.ERROR_NULL);
        }
        ObjectInfo objectInfo = new ObjectInfo(objectName, bucketName);
        var res =
                objectService.deleteObject(objectInfo);
        return HttpResponseEntity.create(res.toResponseBody());
    }

    private final AntPathMatcher antPathMatcher;

    private String readPath(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = antPathMatcher.extractPathWithinPattern(bestMatchingPattern, path);

        if (!arguments.isEmpty()) {
            return "/" + arguments;
        }
        return "";
    }

    private Map<String, String> buildInitialMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put(ObjectMetadataHeaders.CONTENT_TYPE, file.getContentType());
        metadata.put(ObjectMetadataHeaders.OBJECT_LENGTH, "" + file.getSize());
        return metadata;
    }

}
