package org.huel.cloudhub.web.controller.object;

import org.huel.cloudhub.web.service.object.ObjectService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author RollW
 */
@RestController
@ObjectApi
public class ObjectGetController {
    ObjectService objectService;

    //@CrossOrigin(origins = "*")
    //@GetMapping(value = "/{bucketId}/{id}", produces = "*/*")
    // TODO: 待修改
    public byte[] getObjectFile(@PathVariable("bucketId") String bucketId,
                                @PathVariable("id") String fileId) throws IOException {
        // TODO: 为每个存储桶设置权限
        // 例如：存储桶设置了私有权限，则需要提供相应令牌（或者其他能够识别身份的）才可以获取文件
        byte[] bytes = objectService.getObjectDataBytes(bucketId, fileId);
        if (bytes == null) {
            throw new FileNotFoundException("not found resource fileId: " + fileId);
        }
        return bytes;
    }
}
