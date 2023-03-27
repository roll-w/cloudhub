package org.huel.cloudhub.web.data.page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author RollW
 */
public class Page<T> {
    private final int page;// current page
    private final int size;// page size
    private final int total;// total number of items
    private final List<T> data;

    public Page(int page, int size, int total, List<T> data) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotal() {
        return total;
    }

    public List<T> getData() {
        return data;
    }

    public Stream<T> stream() {
        return data.stream();
    }

    public <R> Page<R> transform(Function<T, R> mapper) {
        return new Page<>(
                page, size,
                total,
                data.stream().map(mapper).toList()
        );
    }


    public static <T> Page<T> of(int page, int size,
                                 int total, List<T> data) {
        return new Page<>(page, size, total, data);
    }

    public static <T> Page<T> of(Pageable pageable,
                                 int total, List<T> data) {
        return new Page<>(
                pageable.getPage(),
                pageable.getSize(),
                total, data
        );
    }


    public static <T> Page<T> of(Page<?> raw,
                                 Stream<T> data) {
        return new Page<>(
                raw.getPage(), raw.getSize(),
                raw.getTotal(), data.toList()
        );
    }

    public static <T> Page<T> of() {
        return (Page<T>) PAGE;
    }

    private static final Page<?> PAGE = new Page<>(0, 0, 0, List.of());
}
