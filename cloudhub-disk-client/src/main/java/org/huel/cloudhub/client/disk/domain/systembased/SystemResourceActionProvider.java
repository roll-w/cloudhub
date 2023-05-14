package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.operatelog.Action;
import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface SystemResourceActionProvider extends SystemResourceProvider,
        SystemResourceAuthenticationProvider {
    @Override
    default boolean isAuthentication(SystemResourceKind resourceKind) {
        return supports(resourceKind);
    }

    @NonNull
    @Override
    SystemAuthentication authentication(SystemResource systemResource,
                                        Operator operator, Action action);

    @Override
    boolean supports(SystemResourceKind systemResourceKind);

    @Override
    SystemResource provide(long resourceId, SystemResourceKind systemResourceKind);
}
