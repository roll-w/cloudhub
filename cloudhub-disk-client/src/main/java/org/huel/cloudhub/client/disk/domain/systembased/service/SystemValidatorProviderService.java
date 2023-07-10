package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.systembased.SystemResourceKind;
import org.huel.cloudhub.client.disk.domain.systembased.UnsupportedKindException;
import org.huel.cloudhub.client.disk.domain.systembased.validate.Validator;
import org.huel.cloudhub.client.disk.domain.systembased.validate.ValidatorProvider;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class SystemValidatorProviderService implements ValidatorProvider {
    private final List<Validator> validators;

    public SystemValidatorProviderService(List<Validator> validators) {
        this.validators = validators;
    }

    @Override
    public @NonNull Validator getValidator(@NonNull SystemResourceKind systemResourceKind)
            throws UnsupportedKindException {
        return getValidatorByKind(systemResourceKind);
    }

    private Validator getValidatorByKind(SystemResourceKind systemResourceKind) {
        return validators.stream()
                .filter(validator -> validator.supports(systemResourceKind))
                .findFirst()
                .orElseThrow(() -> new UnsupportedKindException(systemResourceKind));
    }
}
