package org.huel.cloudhub.client.disk.controller.usergroup;

/**
 * @author RollW
 */
public record UserGroupCreateRequest(
        String name,
        String description
) {
}
