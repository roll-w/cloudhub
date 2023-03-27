package org.huel.cloudhub.web.data.page;

/**
 * @author RollW
 */
public record Offset(
        int limit,
        int offset
) implements Pageable {
    public static final Offset DEFAULT = new Offset(10, 0);

    @Override
    public int getPage() {
        return offset / limit;
    }

    @Override
    public int getSize() {
        return limit;
    }
}
