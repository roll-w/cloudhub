package org.huel.cloudhub.client.disk.domain.userstorage.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.authentication.token.AuthenticationTokenService;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageDownloadTokenProvider;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageIdentity;
import org.huel.cloudhub.client.disk.domain.userstorage.StorageType;
import org.huel.cloudhub.client.disk.domain.userstorage.dto.SimpleStorageIdentity;
import org.huel.cloudhub.web.AuthErrorCode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * @author RollW
 */
@Service
public class DownloadTokenServiceImpl implements StorageDownloadTokenProvider {
    private final Cache<String, DownloadToken> downloadTokenCache;

    public DownloadTokenServiceImpl() {
        downloadTokenCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(30))
                .expireAfterAccess(Duration.ofMinutes(30))
                .build();
    }

    @Override
    public String getDownloadToken(StorageIdentity storageIdentity,
                                   Duration duration) {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        long now = System.currentTimeMillis();
        DownloadToken downloadToken = new DownloadToken(
                token,
                storageIdentity.getStorageId(),
                storageIdentity.getStorageType(),
                now + duration.toMillis()
        );
        downloadTokenCache.put(token, downloadToken);
        return downloadToken.token();
    }

    @Override
    public String getDownloadToken(StorageIdentity storageIdentity) {
        return getDownloadToken(storageIdentity, AuthenticationTokenService.MIN_5);
    }

    @Override
    public StorageIdentity verifyDownloadToken(String token) throws AuthenticationException {
        DownloadToken downloadToken = downloadTokenCache.getIfPresent(token);
        if (downloadToken == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_TOKEN_NOT_EXIST);
        }
        if (downloadToken.expireTime() < System.currentTimeMillis()) {
            throw new AuthenticationException(AuthErrorCode.ERROR_TOKEN_EXPIRED);
        }
        return new SimpleStorageIdentity(
                downloadToken.storageId(),
                downloadToken.storageType()
        );
    }

    private record DownloadToken(
            String token,
            long storageId,
            StorageType storageType,
            long expireTime) {
    }
}
