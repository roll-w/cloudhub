package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import org.huel.cloudhub.client.disk.domain.systembased.SimpleSystemAuthentication;
import org.huel.cloudhub.client.disk.domain.systembased.SystemAuthentication;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticationProvider;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceAuthenticationProviderFactory;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.web.AuthErrorCode;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ResourceAuthenticationServiceImpl implements SystemResourceAuthenticationProviderFactory {
    private final List<SystemResourceAuthenticationProvider> authenticationProviders;
    private SystemResourceAuthenticationProvider defaultSystemResourceAuthenticationProvider;

    public ResourceAuthenticationServiceImpl(List<SystemResourceAuthenticationProvider> authenticationProviders) {
        this.authenticationProviders = authenticationProviders;
        defaultSystemResourceAuthenticationProvider = DefaultProvider.INSTANCE;
    }

    @Override
    public SystemResourceAuthenticationProvider getSystemResourceAuthenticationProvider(
            SystemResourceKind resourceKind) {
        SystemResourceAuthenticationProvider systemResourceAuthenticationProvider =
                findFirstAuthenticationProvider(resourceKind);
        if (systemResourceAuthenticationProvider == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NO_HANDLER,
                    "No authentication provider found for resource kind: " + resourceKind +
                            ", or set a default authentication provider.");
        }
        return systemResourceAuthenticationProvider;
    }

    @Override
    public void setDefaultSystemResourceAuthenticationProvider(
            SystemResourceAuthenticationProvider systemResourceAuthenticationProvider) {
        this.defaultSystemResourceAuthenticationProvider = systemResourceAuthenticationProvider;
    }

    private SystemResourceAuthenticationProvider findFirstAuthenticationProvider(
            SystemResourceKind resourceKind) {
        return authenticationProviders.stream()
                .filter(authenticationProvider -> authenticationProvider.isAuthentication(resourceKind))
                .findFirst()
                .orElse(defaultSystemResourceAuthenticationProvider);
    }

    private static class DefaultProvider implements SystemResourceAuthenticationProvider {
        private DefaultProvider() {
        }

        @Override
        public boolean isAuthentication(SystemResourceKind resourceKind) {
            return true;
        }

        @NonNull
        @Override
        public SystemAuthentication authenticate(SystemResource systemResource,
                                                 Operator operator, Action action) {
            return new SimpleSystemAuthentication(systemResource, operator, true);
        }

        static final DefaultProvider INSTANCE = new DefaultProvider();
    }


    public static SystemResourceAuthenticationProvider getDefaultProvider() {
        return DefaultProvider.INSTANCE;
    }
}
