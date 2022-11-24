package org.huel.cloudhub.client.data.dto.object;

import java.util.Map;

/**
 * @author RollW
 */
public record ObjectMetadataSetRequest(
        String bucketName,
        String objectName,
        Map<String, String> metadata) {
}
