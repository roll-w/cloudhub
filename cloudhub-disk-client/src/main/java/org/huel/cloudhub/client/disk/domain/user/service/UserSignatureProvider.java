package org.huel.cloudhub.client.disk.domain.user.service;

/**
 * Provides signature of user to sign the token.
 *
 * @author RollW
 */
public interface UserSignatureProvider {
    String getSignature(long userId);
}
