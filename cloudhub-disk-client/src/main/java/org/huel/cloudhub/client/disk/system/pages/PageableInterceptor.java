package org.huel.cloudhub.client.disk.system.pages;

import org.huel.cloudhub.client.disk.database.DataItem;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;

import java.util.List;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * @author RollW
 */
public interface PageableInterceptor {
    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz);

    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz,
                                  boolean active);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  Class<? extends DataItem> typeClazz,
                                  boolean active);

    <T> Page<T> interceptPageable(Supplier<List<T>> supplier,
                                  Pageable parameter,
                                  LongSupplier countSupplier);

    <T> Page<T> interceptPageable(List<T> list,
                                  Pageable parameter,
                                  LongSupplier countSupplier);

    default <D extends PageableDataTransferObject<T>, T extends DataItem> Page<D> interceptPageable(
            Supplier<List<D>> supplier,
            Pageable parameter,
            PageableDataTransferObject<T> transferObject) {
        return interceptPageable(supplier, parameter, transferObject.convertFrom());
    }
}
