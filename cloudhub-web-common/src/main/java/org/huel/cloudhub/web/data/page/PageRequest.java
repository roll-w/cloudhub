package org.huel.cloudhub.web.data.page;

import java.util.Objects;

/**
 * @author RollW
 */
public class PageRequest implements Pageable {
    private final int page;
    private final int size;

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRequest that = (PageRequest) o;
        return page == that.page && size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(page, size);
    }

    @Override
    public String toString() {
        return "PageRequest{" +
                "page=" + page +
                ", size=" + size +
                '}';
    }
}
