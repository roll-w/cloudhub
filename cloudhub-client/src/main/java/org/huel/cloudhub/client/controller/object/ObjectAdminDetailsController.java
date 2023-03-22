package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.controller.ValidateHelper;
import org.huel.cloudhub.client.data.dto.object.ObjectInfoVo;
import org.huel.cloudhub.client.data.dto.object.ObjectMetadataRemoveRequest;
import org.huel.cloudhub.client.data.dto.object.ObjectMetadataSetRequest;
import org.huel.cloudhub.client.service.object.ObjectErrorCode;
import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectRuntimeException;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.web.HttpResponseEntity;
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
@ObjectAdminApi
public class ObjectAdminDetailsController {
    private final ObjectService objectService;
    private final ObjectMetadataService objectMetadataService;
    private final UserGetter userGetter;
    private final Logger logger = LoggerFactory.getLogger(ObjectAdminDetailsController.class);

    public ObjectAdminDetailsController(ObjectService objectService,
                                        ObjectMetadataService objectMetadataService,
                                        UserGetter userGetter) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.userGetter = userGetter;
    }

    @GetMapping("/get")
    public HttpResponseEntity<List<ObjectInfoVo>> getObjectsInBucket(
            HttpServletRequest request,
            @RequestParam String bucketName) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        List<ObjectInfoVo> vos = objectService.getObjectsInBucket(bucketName)
                .stream().map(ObjectInfoVo::from).toList();
        return HttpResponseEntity.success(vos);
    }

    @GetMapping("/get/detail")
    public HttpResponseEntity<ObjectInfoVo> getObjectDetail(
            HttpServletRequest request,
            @RequestParam String bucketName,
            @RequestParam String objectName) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        ObjectInfoVo vo = ObjectInfoVo.from(
                objectService.getObjectInBucket(bucketName, objectName));
        return HttpResponseEntity.success(vo);
    }

    @GetMapping("/metadata/get")
    public HttpResponseEntity<Map<String, String>> getObjectMetadata(HttpServletRequest request,
                                                                     String bucketName,
                                                                     String objectName) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        Map<String, String> metadata = objectMetadataService
                .getObjectMetadata(bucketName, objectName);
        if (metadata == null) {
            throw new ObjectRuntimeException(ObjectErrorCode.ERROR_OBJECT_NOT_EXIST);
        }
        return HttpResponseEntity.success(metadata);
    }

    @PostMapping("/metadata/set")
    public HttpResponseEntity<Void> setObjectMetadata(HttpServletRequest request,
                                                      @RequestBody ObjectMetadataSetRequest setRequest) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        objectMetadataService.addObjectMetadataWithCheck(
                setRequest.bucketName(),
                setRequest.objectName(),
                setRequest.metadata()
        );
        return HttpResponseEntity.success();
    }

    @PostMapping("/metadata/remove")
    public HttpResponseEntity<Void> removeObjectMetadata(HttpServletRequest request,
                                                         @RequestBody ObjectMetadataRemoveRequest removeRequest) {
        ValidateHelper.validateUserAdmin(request, userGetter);

        objectMetadataService.removeObjectMetadata(
                removeRequest.bucketName(),
                removeRequest.objectName(),
                removeRequest.removeKeys()
        );
        return HttpResponseEntity.success();
    }
}
