package org.huel.cloudhub.client.disk.domain.systembased;

/**
 * @author RollW
 */
public interface ContextThreadAware<C extends SystemContext> {
    ContextThread<C> assambleContextThread(C context);

    ContextThread<C> getContextThread();

    void clearContextThread();
}
