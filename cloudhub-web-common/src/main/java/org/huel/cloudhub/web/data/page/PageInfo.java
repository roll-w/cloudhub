package org.huel.cloudhub.web.data.page;

/**
 * @author RollW
 */
public record PageInfo(
        int page,
        int size
) implements Pageable {
    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }
}
