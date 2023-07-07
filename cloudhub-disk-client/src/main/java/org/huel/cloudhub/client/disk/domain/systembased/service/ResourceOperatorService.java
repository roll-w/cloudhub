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
            SystemResource systemResource) {
        return getSystemResourceOperator(systemResource, true);
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource, boolean checkDelete) {
        SystemResourceOperatorFactory systemResourceOperatorFactory =
                findFirstOf(systemResource.getSystemResourceKind());
        SystemResourceOperator systemResourceOperator =
                systemResourceOperatorFactory.createResourceOperator(systemResource, checkDelete);
        try {
            return (T) systemResourceOperator;
        } catch (ClassCastException e) {
            throw noFactoryConfiguredForKindAndType(
                    systemResource.getSystemResourceKind(),
                    systemResourceOperator.getClass(),
                    e.getMessage()
            );
        }
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind,
            boolean checkDelete) {
        SystemResourceOperatorFactory systemResourceOperatorFactory = findFirstOf(
                targetSystemResourceKind
        );
        SystemResourceOperator systemResourceOperator = systemResourceOperatorFactory.createResourceOperator(
                systemResource,
                targetSystemResourceKind,
                checkDelete
        );
        try {
            return (T) systemResourceOperator;
        } catch (ClassCastException e) {
            throw noFactoryConfiguredForKindAndType(
                    systemResource.getSystemResourceKind(),
                    systemResourceOperator.getClass(),
                    e.getMessage()
            );
        }
    }

    @Override
    public <T extends SystemResourceOperator> T getSystemResourceOperator(
            SystemResource systemResource,
            SystemResourceKind targetSystemResourceKind) {
        return getSystemResourceOperator(
                systemResource,
                targetSystemResourceKind,
                true
        );
    }

    private SystemResourceOperatorFactory findFirstOf(SystemResourceKind kind) {
        return systemResourceOperatorFactories.stream()
                .filter(factory -> factory.supports(kind))
                .findFirst()
                .orElseThrow((() -> noFactoryConfiguredForKind(kind)));
    }

    private IllegalArgumentException noFactoryConfiguredForKindAndType(
            SystemResourceKind kind, Class<? extends SystemResourceOperator> clazz,
            String message) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + " and type:" + clazz + ". " + message);
    }

    private IllegalArgumentException noFactoryConfiguredForKind(
            SystemResourceKind kind) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + ".");
    }

    private IllegalArgumentException noFactoryConfiguredForBothKind(
            SystemResourceKind kind, SystemResourceKind targetSystemResourceKind) {
        return new IllegalArgumentException("No system resource operator factory configured for kind:"
                + kind + " and target kind:" + targetSystemResourceKind + ".");
    }
}
