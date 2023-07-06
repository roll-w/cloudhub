package org.huel.cloudhub.client.disk.configuration.filter;

import com.google.common.base.Strings;
import org.huel.cloudhub.client.disk.common.ParameterFailedException;
import org.huel.cloudhub.client.disk.domain.systembased.ContextThread;
import org.huel.cloudhub.client.disk.domain.systembased.paged.PageableContext;
import org.huel.cloudhub.client.disk.domain.systembased.service.PageableContextFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RollW
 */
@Component
public class ContextInitializeFilter extends OncePerRequestFilter {
    private final PageableContextFactory pageableContextFactory;

    public ContextInitializeFilter(PageableContextFactory pageableContextFactory) {
        this.pageableContextFactory = pageableContextFactory;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        PageableContext context = fromRequest(request);
        ContextThread<PageableContext> contextThread =
                pageableContextFactory.assambleContextThread(context);
        try {
            filterChain.doFilter(request, response);
        } finally {
            pageableContextFactory.clearContextThread();
        }
    }

    private PageableContext fromRequest(HttpServletRequest request) {
        String page = request.getParameter("page");
        String size = request.getParameter("size");

        try {
            int pageInt = Strings.isNullOrEmpty(page) ? 1 : Integer.parseInt(page);
            int sizeInt = Strings.isNullOrEmpty(size) ? 10 : Integer.parseInt(size);
            return new PageableContext(pageInt, sizeInt);
        } catch (NumberFormatException e) {
            throw new ParameterFailedException(e.getMessage());
        }
    }
}
