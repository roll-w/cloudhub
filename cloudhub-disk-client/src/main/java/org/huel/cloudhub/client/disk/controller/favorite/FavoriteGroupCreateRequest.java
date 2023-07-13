package org.huel.cloudhub.client.disk.controller.favorite;

/**
 * @author RollW
 */
public record FavoriteGroupCreateRequest(
    String name,
    boolean isPublic
) {
}
