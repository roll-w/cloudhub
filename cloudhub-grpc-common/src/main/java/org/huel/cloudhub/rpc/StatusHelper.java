package org.huel.cloudhub.rpc;

import io.grpc.Status;
import io.grpc.StatusException;
import io.grpc.StatusRuntimeException;

/**
 * @author RollW
 */
public class StatusHelper {

    public static boolean isCancelled(Throwable t) {
        if (t == null) {
            return false;
        }
        if (t instanceof StatusRuntimeException statusRuntimeException) {
            return statusRuntimeException.getStatus().getCode() == Status.Code.CANCELLED;
        }
        if (t instanceof StatusException statusException) {
            return statusException.getStatus().getCode() == Status.Code.CANCELLED;
        }
        return isCancelled(t.getCause());
    }

    private StatusHelper() {
    }
}
