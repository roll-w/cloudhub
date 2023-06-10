package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
public interface SystemResourceAuthenticationProvider {
    boolean isAuthentication(SystemResourceKind resourceKind);

    @NonNull
    SystemAuthentication authenticate(SystemResource systemResource,
                                      Operator operator, Action action);

    @NonNull
    default List<SystemAuthentication> authenticate(
            @NonNull List<? extends SystemResource> systemResources,
            Operator operator, Action action) {
        return systemResources.stream()
                .map(systemResource -> authenticate(systemResource, operator, action))
                .toList();
    }
}
