package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceAuthenticationProviderFactory {
    SystemResourceAuthenticationProvider
    getSystemResourceAuthenticationProvider(SystemResourceKind resourceKind);

    void setDefaultSystemResourceAuthenticationProvider(
            SystemResourceAuthenticationProvider systemResourceAuthenticationProvider);
}
