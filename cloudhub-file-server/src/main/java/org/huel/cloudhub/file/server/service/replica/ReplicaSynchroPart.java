package org.huel.cloudhub.file.server.service.replica;

import org.huel.cloudhub.file.fs.container.Container;

/**
 * @author RollW
 */
public class ReplicaSynchroPart {
    public static final int FULL = -1;

    private final Container container;
    private final int start;
    private final int end;
    private final int count;

    public ReplicaSynchroPart(Container container, int start, int end) {
        this.container = container;
        this.start = start;
        this.end = end;
        this.count = calcCount(container, start, end);
    }

    private int calcCount(Container container, int start, int end) {
        if (start < 0) {
            return container.getIdentity().blockLimit();
        }
        return end - start + 1;
    }

    public Container getContainer() {
        return container;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int getCount() {
        return count;
    }
}
