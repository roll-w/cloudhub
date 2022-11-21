package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.data.dto.user.UserInfo;
import org.huel.cloudhub.client.service.bucket.BucketService;
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
    // TODO: 返回值等待修改
    public byte[] getObjectFile(HttpServletRequest request,
                                @PathVariable("bucketName") String bucketName) throws IOException {


        return null;
    }

    @PostMapping(value = "/{bucketName}/**", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 待修改
    public HttpResponseEntity<String> uploadObject(HttpServletRequest request,
                                                   @PathVariable("bucketName") String bucketName,
                                                   @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        return null;
    }

    @DeleteMapping(value = "/{bucketName}/**")
    public HttpResponseEntity<String> deleteObject(HttpServletRequest request,
                                                   @PathVariable("bucketName") String bucketName) {
        UserInfo userInfo = userGetter.getCurrentUser(request);
        bucketService.allowWrite(userInfo, bucketName);

        final String objectName = readPath(request);
        if (objectName.isEmpty())  {
            return HttpResponseEntity.failure("Not valid object name.",
                    ErrorCode.ERROR_NULL);
        }

        return null;
    }

    private final AntPathMatcher antPathMatcher;

    private String readPath(HttpServletRequest request) {
        final String path =
                request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
        final String bestMatchingPattern =
                request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE).toString();

        String arguments = antPathMatcher.extractPathWithinPattern(bestMatchingPattern, path);

        if (!arguments.isEmpty()) {
            return  "/" + arguments;
        }
        return "";
    }

}
