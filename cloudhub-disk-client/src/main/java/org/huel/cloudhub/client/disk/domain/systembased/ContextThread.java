package org.huel.cloudhub.client.disk.domain.systembased;

import java.util.concurrent.Callable;

/**
 * @author RollW
 */
public interface ContextThread<C extends SystemContext> {
    C getContext();

    void setContext(C systemThreadContext);

    boolean hasContext();

    void clearContext();

    default void callWithContext(C systemThreadContext, Runnable runnable) {
        C old = getContext();
        setContext(systemThreadContext);
        try {
            call(runnable);
        } finally {
            setContext(old);
        }
    }

    default <T> T callWithContext(C systemThreadContext,
                                  Callable<T> callable) throws Exception {
        C old = getContext();
        setContext(systemThreadContext);
        try {
            return call(callable);
        } finally {
            setContext(old);
        }
    }

    <T> T call(Callable<T> callable) throws Exception;

    void call(Runnable callable);
}
