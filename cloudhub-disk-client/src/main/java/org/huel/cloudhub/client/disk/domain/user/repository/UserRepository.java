package org.huel.cloudhub.client.disk.domain.user.repository;

import org.huel.cloudhub.client.disk.common.CacheNames;
import org.huel.cloudhub.client.disk.database.DiskDatabase;
import org.huel.cloudhub.client.disk.database.dao.UserDao;
import org.huel.cloudhub.client.disk.database.repository.BaseRepository;
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
                          CacheManager cacheManager) {
        super(database.getUserDao(), cacheManager);
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

    public User getUserById(long id) {
        User cached = userCache.get(id, User.class);
        if (cached != null) {
            return cached;
        }
        User queried = userDao.getUserById(id);
        updateCache(queried);
        return queried;
    }

    public User getUserByName(String name) {
        User cached = userCache.get(name, User.class);
        if (cached != null) {
            return cached;
        }
        User queried = userDao.getUserByName(name);
        updateCache(queried);
        return queried;
    }

    private Long getUserIdByName(String name) {
        return userDao.getUserIdByName(name);
    }

    public boolean isExistByName(String name) {
        return !User.isInvalidId(getUserIdByName(name));
    }

    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
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

    private void removeCache(User user) {
        userCache.evictIfPresent(user.getId());
        userCache.evictIfPresent(user.getUsername());
        userCache.evictIfPresent(user.getEmail());
    }
}
