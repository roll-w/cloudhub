package org.huel.cloudhub.client.disk.domain.systembased.validate;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.UnsupportedKindException;
import space.lingu.NonNull;

/**
 * @author RollW
 */
public interface ValidatorProvider {
    @NonNull
    Validator getValidator(@NonNull SystemResourceKind systemResourceKind)
            throws UnsupportedKindException;
}
