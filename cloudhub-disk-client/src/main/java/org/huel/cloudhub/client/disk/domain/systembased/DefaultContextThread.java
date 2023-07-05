package org.huel.cloudhub.client.disk.domain.systembased;

import java.util.concurrent.Callable;

/**
 * @author RollW
 */
public class DefaultContextThread<C extends SystemContext> implements ContextThread<C> {
    private C systemThreadContext;

    public DefaultContextThread(C systemThreadContext) {
        this.systemThreadContext = systemThreadContext;
    }

    @Override
    public C getContext() {
        return systemThreadContext;
    }

    @Override
    public void setContext(C systemThreadContext) {
        this.systemThreadContext = systemThreadContext;
    }

    @Override
    public boolean hasContext() {
        return systemThreadContext != null;
    }

    @Override
    public void clearContext() {
        this.systemThreadContext = null;
    }

    @Override
    public <T> T call(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    public void call(Runnable callable) {
        callable.run();
    }
}
