package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.huel.cloudhub.web.BusinessRuntimeException;
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
                                 SystemResourceKind systemResourceKind)
            throws BusinessRuntimeException, UnsupportedKindException {
        SystemResourceProvider systemResourceProvider = findFirstProvider(systemResourceKind);
        return systemResourceProvider.provide(resourceId, systemResourceKind);
    }

    @Override
    public SystemResource locate(SystemResource systemResource) {
        return locate(systemResource.getResourceId(), systemResource.getSystemResourceKind());
    }

    private SystemResourceProvider findFirstProvider(SystemResourceKind systemResourceKind) {
        return systemResourceProviders.stream()
                .filter(systemResourceProvider -> systemResourceProvider.supports(systemResourceKind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No provider found for resource kind: " + systemResourceKind));
    }
}
