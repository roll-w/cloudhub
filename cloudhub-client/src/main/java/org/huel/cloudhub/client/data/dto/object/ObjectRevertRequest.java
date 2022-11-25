package org.huel.cloudhub.client.data.dto.object;

/**
 * @author RollW
 */
public record ObjectRevertRequest(
        String bucketName,
        String objectName,
        long version
) {
}
