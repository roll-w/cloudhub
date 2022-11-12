package org.huel.cloudhub.client.service.object;

import org.huel.cloudhub.common.MessagePackage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author RollW
 */
public interface ObjectService {
    // TODO: 此接口尚未修改完成
    MessagePackage<String> saveObject(HttpServletRequest request, byte[] data) throws IOException;

    MessagePackage<String> saveObject(HttpServletRequest request, InputStream stream) throws IOException;

    byte[] getObjectDataBytes(String bucketId, String objectName) throws IOException;
}
