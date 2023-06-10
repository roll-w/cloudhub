package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface SystemResourceOperatorProvider {
    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource, Class<T> clazz);

    <T extends SystemResourceOperator> T
    getSystemResourceOperator(SystemResource systemResource, Class<T> clazz, boolean checkDelete);
}
