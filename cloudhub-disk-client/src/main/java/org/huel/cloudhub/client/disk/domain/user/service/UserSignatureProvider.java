package org.huel.cloudhub.client.disk.domain.user.service;

import org.huel.cloudhub.client.disk.domain.user.UserIdentity;

/**
 * Provides signature of user to sign the token.
 *
 * @author RollW
 */
public interface UserSignatureProvider {
    String getSignature(long userId);

    String getSignature(UserIdentity userIdentity);
}
