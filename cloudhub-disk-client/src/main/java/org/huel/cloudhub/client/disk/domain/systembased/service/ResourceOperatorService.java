package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.systembased.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ResourceOperatorService implements SystemResourceOperatorProvider {
    private final List<SystemResourceOperatorFactory> systemResourceOperatorFactories;

    public ResourceOperatorService(List<SystemResourceOperatorFactory> systemResourceOperatorFactories) {
        this.systemResourceOperatorFactories = systemResourceOperatorFactories;
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource, Class<T> clazz) {
        return getSystemResourceOperator(systemResource, clazz, false);
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource, Class<T> clazz, boolean checkDelete) {
        SystemResourceOperatorFactory systemResourceOperatorFactory =
                findFirstOf(systemResource.getSystemResourceKind(), clazz);
        SystemResourceOperator systemResourceOperator =
                systemResourceOperatorFactory.createResourceOperator(systemResource, checkDelete);
        try {
            return clazz.cast(systemResourceOperator);
        } catch (ClassCastException e) {
            throw noFactoryConfiguredForKindAndType(
                    systemResource.getSystemResourceKind(),
                    clazz
            );
        }
    }

    private SystemResourceOperatorFactory findFirstOf(SystemResourceKind kind,
                                                      Class<? extends SystemResourceOperator> clazz) {
        return systemResourceOperatorFactories.stream()
                .filter(factory -> factory.supports(kind))
                .filter(factory -> factory.isAssignableTo(clazz))
                .findFirst()
                .orElseThrow((() -> noFactoryConfiguredForKindAndType(kind, clazz)));
    }

    private IllegalArgumentException noFactoryConfiguredForKindAndType(
            SystemResourceKind kind, Class<? extends SystemResourceOperator> clazz) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + " and type:" + clazz + ".");
    }
}
