package org.huel.cloudhub.client.disk.domain.systembased.paged;

import org.huel.cloudhub.client.disk.domain.systembased.SystemContext;
import org.huel.cloudhub.web.data.page.Page;
import org.huel.cloudhub.web.data.page.Pageable;
import space.lingu.light.Order;

import java.util.List;

/**
 * @author RollW
 */
public class PageableContext implements SystemContext, Pageable {
    private int page;
    private int size;
    private boolean includeDeleted = false;

    private int total = 0;

    private Order order = Order.DESC;
    private String orderBy = "id";

    public PageableContext(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageableContext() {
        this(0, 0);
    }

    public int getTotal() {
        return total;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getSize() {
        return size;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void addTotal(int total) {
        this.total += total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public <T> Page<T> toPage(List<T> data) {
        return Page.of(this, total, data);
    }

    public static PageableContext of(int page, int size) {
        return new PageableContext(page, size);
    }

    public static PageableContext of(Pageable pageable) {
        return new PageableContext(pageable.getPage(), pageable.getSize());
    }

    @Override
    public Object getObject(String key) {
        return switch (key) {
            case "page" -> page;
            case "size" -> size;
            case "total" -> total;
            default -> null;
        };
    }
}
