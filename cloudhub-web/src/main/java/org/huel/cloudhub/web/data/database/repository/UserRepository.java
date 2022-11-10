package org.huel.cloudhub.web.data.database.repository;

import org.huel.cloudhub.web.data.database.CloudhubDatabase;
import org.huel.cloudhub.web.data.database.dao.UserDao;
import org.huel.cloudhub.web.data.entity.user.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

/**
 * @author RollW
 */
@Repository
public class UserRepository {
    private final UserDao userDao;

    public UserRepository(CloudhubDatabase database) {
        this.userDao = database.getUserDao();
    }

    public boolean isExistByEmail(String email) {
        return userDao.getUsernameByEmail(email) != null;
    }

    public boolean isExistById(long id) {
        return userDao.getUsernameById(id) != null;
    }

    @Async
    public void save(User user) {
        insertOrUpdate(user);
    }

    private void insertOrUpdate(User user) {
        if (isExistById(user.getId())) {
            userDao.update(user);
            return;
        }
        userDao.insert(user);
    }

    public long insert(User user) {
        return userDao.insert(user);
    }

    @Async
    public void update(User user) {
        userDao.update(user);
    }

    public User getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    public User getUserByName(String username) {
        return userDao.getUserByName(username);
    }
}
