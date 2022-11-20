package org.huel.cloudhub.client.controller.object;

import org.huel.cloudhub.client.service.object.ObjectMetadataService;
import org.huel.cloudhub.client.service.object.ObjectService;
import org.huel.cloudhub.client.service.user.UserGetter;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private final UserGetter userGetter;


    public ObjectController(ObjectService objectService,
                            ObjectMetadataService objectMetadataService,
                            UserGetter userGetter) {
        this.objectService = objectService;
        this.objectMetadataService = objectMetadataService;
        this.userGetter = userGetter;
    }

    //@CrossOrigin(origins = "*")
    @GetMapping(value = "/{bucketName}/{objectName}")
    // TODO: 返回值等待修改
    public byte[] getObjectFile(@PathVariable("bucketName") String bucketName,
                                @PathVariable("objectName") String objectName) throws IOException {
        // TODO: 为每个存储桶设置权限
        // 例如：存储桶设置了私有权限，则需要提供相应令牌（或者其他能够识别身份的）才可以获取文件

        return null;
    }


    @PostMapping(value = "/{bucketId}/{objectName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 待修改
    public HttpResponseEntity<String> uploadObject(HttpServletRequest request,
                                                   @PathVariable("bucketId") String bucketId,
                                                   @PathVariable("objectName") String objectName,
                                                   @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        return null;
    }

    @DeleteMapping(value = "/{bucketId}/{objectName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResponseEntity<String> deleteObject(HttpServletRequest request,
                                                   @PathVariable("bucketId") String bucketId,
                                                   @PathVariable("objectName") String objectName) {
        return null;
    }
}
