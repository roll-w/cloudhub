package org.huel.cloudhub.client.disk.domain.share.repository;

import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserShareDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.share.UserShare;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class UserShareRepository extends BaseRepository<UserShare> {
    private final UserShareDao userShareDao;

    protected UserShareRepository(DiskDatabase database) {
        super(database.getUserShareDao());
        userShareDao = database.getUserShareDao();
    }
}
