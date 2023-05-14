package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResource;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceLocator;
import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ResourceLocatorImpl implements SystemResourceLocator {
    private final List<SystemResourceProvider> systemResourceProviders;

    public ResourceLocatorImpl(List<SystemResourceProvider> systemResourceProviders) {
        this.systemResourceProviders = systemResourceProviders;
    }

    @Override
    public SystemResource locate(long resourceId,
                                 SystemResourceKind systemResourceKind) {
        SystemResourceProvider systemResourceProvider = findFirstProvider(systemResourceKind);
        return systemResourceProvider.provide(resourceId, systemResourceKind);
    }

    private SystemResourceProvider findFirstProvider(SystemResourceKind systemResourceKind) {
        return systemResourceProviders.stream()
                .filter(systemResourceProvider -> systemResourceProvider.supports(systemResourceKind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No provider found for resource kind: " + systemResourceKind));
    }
}
