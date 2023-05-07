package org.huel.cloudhub.client.disk.system.pages;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author RollW
 */
@Component
public class PagesCachePageableInterceptor implements PageableInterceptor {
    private final PagesCache pagesCache;

    public PagesCachePageableInterceptor(PagesCache pagesCache) {
        this.pagesCache = pagesCache;
    }

    @Override
    public <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                         Pageable parameter,
                                         Class<? extends DataItem> typeClazz) {
        return interceptPageable(supplier, parameter, typeClazz, false);
    }

    @Override
    public <T> Page<T> interceptPageable(List<T> list, Pageable parameter,
                                         Class<? extends DataItem> typeClazz) {
        return interceptPageable(list, parameter, typeClazz, false);
    }

    @Override
    public <T> Page<T> interceptPageable(Supplier<List<T>> supplier, Pageable parameter,
                                         Class<? extends DataItem> typeClazz,
                                         boolean active) {
        long count = getCount(active, typeClazz);
        return Page.of(parameter, count, supplier.get());
    }

    @Override
    public <T> Page<T> interceptPageable(List<T> list, Pageable parameter,
                                         Class<? extends DataItem> typeClazz,
                                         boolean active) {
        long count = getCount(active, typeClazz);
        return Page.of(parameter, count, list);
    }

    private long getCount(boolean active, Class<? extends DataItem> typeClazz) {
        return active
                ? pagesCache.getActiveCount(typeClazz)
                : pagesCache.getCount(typeClazz);
    }
}
