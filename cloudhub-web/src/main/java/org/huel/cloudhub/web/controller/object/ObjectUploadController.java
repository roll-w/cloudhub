package org.huel.cloudhub.web.controller.object;

import org.huel.cloudhub.common.HttpResponseEntity;
import org.huel.cloudhub.web.service.object.ObjectService;
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
@ObjectApi
public class ObjectUploadController {
    ObjectService objectService;


    // @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // TODO: 待修改
    public HttpResponseEntity<String> uploadObject(HttpServletRequest request,
                                                   @RequestPart(name = "object") MultipartFile file)
            throws IOException {
        String contentType = file.getContentType();
        logger.info("upload file, name:{}, size: {}, content-type: {}",
                file.getName(), file.getSize(), file.getContentType());
        return HttpResponseEntity.create(
                objectService.saveObject(request, file.getBytes()).toResponseBody()
        );
    }


    private final Logger logger = LoggerFactory.getLogger(ObjectUploadController.class);

}
