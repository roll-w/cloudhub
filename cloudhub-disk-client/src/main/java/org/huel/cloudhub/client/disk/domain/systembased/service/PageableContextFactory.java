package org.huel.cloudhub.client.disk.domain.systembased.service;

import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThreadAware;
import org.huel.cloudhub.client.disk.domain.systembased.DefaultContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class PageableContextFactory implements ContextThreadAware<PageableContext> {
    private final ThreadLocal<ContextThread<PageableContext>> pageableContextThreadLocal = new ThreadLocal<>();

    @Override
    public ContextThread<PageableContext> assambleContextThread(PageableContext context) {
        ContextThread<PageableContext> contextThread = pageableContextThreadLocal.get();
        if (contextThread == null) {
            contextThread = new DefaultContextThread<>(context);
            pageableContextThreadLocal.set(contextThread);
        }
        return contextThread;
    }

    @Override
    public ContextThread<PageableContext> getContextThread() {
        ContextThread<PageableContext> thread =
                pageableContextThreadLocal.get();
        if (thread != null) {
            return thread;
        }
        return assambleContextThread(null);
    }

    @Override
    public void clearContextThread() {
        pageableContextThreadLocal.remove();
    }

}
