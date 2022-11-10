package org.huel.cloudhub.web.controller.file;

import org.huel.cloudhub.common.ErrorCode;
import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.web.service.file.ObjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author RollW
 */
@RestController
@FileApi
public class FileUploadController {
    ObjectService objectService;


    // @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 待修改
    public HttpResponseEntity<String> uploadImage(HttpServletRequest request,
                                                  @RequestPart(name = "image") MultipartFile file)
            throws IOException {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return HttpResponseEntity.failure("Non-image format files or unrecognized.",
                    ErrorCode.ERROR_FILE_UNMATCHED);
        }
        logger.info("upload file, name:{}, size: {}, content-type: {}",
                file.getName(), file.getSize(), file.getContentType());
        return HttpResponseEntity.create(
                objectService.saveObject(request, file.getBytes()).toResponseBody()
        );
    }


    private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

}
