package org.huel.cloudhub.client.data.dto.object;

import java.util.List;

/**
 * @author RollW
 */
public record ObjectMetadataRemoveRequest(
        String bucketName,
        String objectName,
        List<String> removeKeys) {
}
