package org.huel.cloudhub.objectstorage.data.dto.object;

/**
 * @author RollW
 */
public record ObjectRevertRequest(
        String bucketName,
        String objectName,
        long version
) {
}
