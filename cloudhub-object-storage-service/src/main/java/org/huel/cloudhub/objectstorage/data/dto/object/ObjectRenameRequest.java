package org.huel.cloudhub.objectstorage.data.dto.object;

/**
 * @author RollW
 */
public record ObjectRenameRequest(
        String bucketName,
        String objectName,
        String newName
) {
}
