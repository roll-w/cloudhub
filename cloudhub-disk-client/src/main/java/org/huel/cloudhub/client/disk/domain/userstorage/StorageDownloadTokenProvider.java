package org.huel.cloudhub.client.disk.domain.userstorage;

import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;

import java.time.Duration;

/**
 * @author RollW
 */
public interface StorageDownloadTokenProvider {
    String getDownloadToken(StorageIdentity storageIdentity, Duration duration);

    String getDownloadToken(StorageIdentity storageIdentity);

    StorageIdentity verifyDownloadToken(String token) throws AuthenticationException;
}
