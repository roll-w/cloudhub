package org.huel.cloudhub.client.disk.domain.operatelog.context;

/**
 * @author RollW
 */
public class OperationContextHolder {
    private static final ThreadLocal<OperationContext> THREAD_LOCAL = new ThreadLocal<>();

    public static void setContext(OperationContext operationContext) {
        THREAD_LOCAL.set(operationContext);
    }

    public static OperationContext getContext() {
        OperationContext context = THREAD_LOCAL.get();
        if (context == null) {
            THREAD_LOCAL.set(new OperationContext());
        }
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static boolean isPresent() {
        return THREAD_LOCAL.get() != null;
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }


    private OperationContextHolder() {
    }
}
