package org.huel.cloudhub.web.controller.object;

import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.web.service.object.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
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

    ObjectService objectService;

    //@CrossOrigin(origins = "*")
    @GetMapping(value = "/{bucketId}/{objectName}", produces = "*/*")
    // TODO: 返回值等待修改
    public byte[] getObjectFile( @PathVariable("bucketId")  String bucketId,
                                 @PathVariable("objectName") String objectName) throws IOException {
        // TODO: 为每个存储桶设置权限
        // 例如：存储桶设置了私有权限，则需要提供相应令牌（或者其他能够识别身份的）才可以获取文件
        byte[] bytes = objectService.getObjectDataBytes(bucketId, objectName);
        if (bytes == null) {
            throw new FileNotFoundException("not found resource objectName: " + objectName);
        }
        return bytes;
    }


    @PostMapping(value = "/{bucketId}/{objectName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 待修改
    public HttpResponseEntity<String> uploadObject(HttpServletRequest request,
                                                   @PathVariable("bucketId") String bucketId,
                                                   @PathVariable("objectName") String objectName,
                                                   @RequestPart(name = "object") MultipartFile objectFile)
            throws IOException {
        String contentType = objectFile.getContentType();
        logger.info("upload objectFile, name:{}, size: {}, content-type: {}",
                objectFile.getName(), objectFile.getSize(), objectFile.getContentType());
        return HttpResponseEntity.create(
                objectService.saveObject(request, objectFile.getBytes()).toResponseBody()
        );
    }

    @DeleteMapping(value = "/{bucketId}/{objectName}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public HttpResponseEntity<String> deleteObject(HttpServletRequest request,
                                                   @PathVariable("bucketId") String bucketId,
                                                   @PathVariable("objectName") String objectName) {
        return null;
    }
}
