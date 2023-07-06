package org.huel.cloudhub.client.disk.domain.user.repository;

import org.huel.cloudhub.client.disk.common.CacheNames;
import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.user.User;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author RollW
 */
@Repository
public class UserRepository extends BaseRepository<User> {
    private final UserDao userDao;
    private final Cache userCache;

    public UserRepository(DiskDatabase database,
                          ContextThreadAware<PageableContext> pageableContextThreadAware,
                          CacheManager cacheManager) {
        super(database.getUserDao(), pageableContextThreadAware, cacheManager);
        this.userDao = database.getUserDao();
        this.userCache = cacheManager.getCache(CacheNames.USERS);
    }

    public long insertUser(User user) {
        if (user == null) {
            return -1;
        }
        long id = userDao.insertReturns(user);
        User newUser = user
                .toBuilder()
                .setId(id)
                .build();
        updateCache(newUser);
        return id;
    }

    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }

    @Async
    public void makeUserEnabled(User user) {
        userDao.updateEnabledByUser(user.getId(), true);
        User newUser = user.toBuilder()
                .setEnabled(true)
                .build();
        updateCache(newUser);
    }

    public User getUserByName(String name) {
        Long userId = getUserIdByName(name);
        if (User.isInvalidId(userId)) {
            return null;
        }
        return getById(userId);
    }

    private Long getUserIdByName(String name) {
        return userDao.getUserIdByName(name);
    }

    public boolean isExistByName(String name) {
        return !User.isInvalidId(getUserIdByName(name));
    }

    public User getUserByEmail(String email) {
        Long userId = getUserIdByEmail(email);
        if (User.isInvalidId(userId)) {
            return null;
        }
        return getById(userId);
    }

    private Long getUserIdByEmail(String email) {
        return userDao.getUserIdByEmail(email);
    }

    public boolean isExistByEmail(String email) {
        return !User.isInvalidId(getUserIdByEmail(email));
    }

    public List<User> getAll() {
        return userDao.getAll();
    }

    private final AtomicBoolean hasUsers = new AtomicBoolean(false);

    public boolean hasUsers() {
        if (hasUsers.get()) {
            return hasUsers.get();
        }
        boolean has = userDao.hasUsers() != null;
        hasUsers.set(has);
        return hasUsers.get();
    }

    private void updateCache(User user) {
        if (user == null) {
            return;
        }
        userCache.put(user.getId(), user);
        userCache.put(user.getUsername(), user);
        userCache.put(user.getEmail(), user);
    }

}
