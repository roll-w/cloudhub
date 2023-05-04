package org.huel.cloudhub.client.disk.database.dao;

import org.huel.cloudhub.client.disk.domain.share.UserShare;
import space.lingu.light.Dao;

/**
 * @author RollW
 */
@Dao
public interface UserShareDao extends AutoPrimaryBaseDao<UserShare> {

    @Override
    default String getTableName() {
        return "user_share";
    }
}
