package org.huel.cloudhub.client.data.dto.object;

/**
 * @author RollW
 */
public record ObjectRenameRequest(
        String bucketName,
        String objectName,
        String newName
) {
}
