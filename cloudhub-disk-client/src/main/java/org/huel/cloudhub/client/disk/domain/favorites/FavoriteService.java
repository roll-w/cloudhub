package org.huel.cloudhub.client.disk.domain.favorites;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;

/**
 * @author RollW
 */
public interface FavoriteService {
    void createFavoriteGroup(String name, boolean isPublic, Operator of);
}
