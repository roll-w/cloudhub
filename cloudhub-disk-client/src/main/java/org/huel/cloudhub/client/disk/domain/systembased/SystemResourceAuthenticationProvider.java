package org.huel.cloudhub.client.disk.domain.systembased;

import org.huel.cloudhub.client.disk.domain.operatelog.Operator;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface SystemResourceAuthenticationProvider {
    boolean isAuthentication(SystemResourceKind resourceKind);

    @NonNull
    SystemAuthentication authentication(SystemResource systemResource,
                                        Operator operator);
}
