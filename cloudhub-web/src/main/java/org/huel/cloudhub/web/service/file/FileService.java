package org.huel.cloudhub.web.service.file;

import org.huel.cloudhub.common.MessagePackage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public interface FileService {
    // !!!!
    // 此接口尚未修改完成
    // !!!!
    MessagePackage<String> saveImage(HttpServletRequest request, byte[] data) throws IOException;

    MessagePackage<String> saveImage(HttpServletRequest request, InputStream stream) throws IOException;

    byte[] getFileBytes(String bucketId, String fileId) throws IOException;
}
