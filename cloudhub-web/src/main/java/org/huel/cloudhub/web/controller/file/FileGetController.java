package org.huel.cloudhub.web.controller.file;

import org.huel.cloudhub.web.service.file.ObjectService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author RollW
 */
@RestController
@FileApi
public class FileGetController {
    //    final FileService fileService;
    ObjectService objectService;

//    public FileGetController(FileService fileService) {
//        this.fileService = fileService;
//    }

    //@CrossOrigin(origins = "*")
    //@GetMapping(value = "/{bucketId}/{id}", produces = "*/*")
    // TODO: 待修改
    public byte[] getImage(@PathVariable("bucketId") String bucketId,
                           @PathVariable("id") String fileId) throws IOException {
        // TODO: 为每个存储桶设置权限

        byte[] bytes = objectService.getObjectDataBytes(bucketId, fileId);
        if (bytes == null) {
            throw new FileNotFoundException("not found resource fileId: " + fileId);
        }
        return bytes;
    }
}
