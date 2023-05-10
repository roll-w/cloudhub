package org.huel.cloudhub.client.disk.configuration.filter;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.domain.operatelog.OperateLogger;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContext;
import org.huel.cloudhub.client.disk.domain.operatelog.context.OperationContextHolder;
import org.huel.cloudhub.client.disk.domain.operatelog.dto.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class OperateLogFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(OperateLogFilter.class);
    private final OperateLogger operateLogger;

    public OperateLogFilter(OperateLogger operateLogger) {
        this.operateLogger = operateLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        ApiContextHolder.ApiContext apiContext = ApiContextHolder.getContext();
        OperationContext context = new OperationContext();
        OperationContextHolder.setContext(context);
        context.setOperator(apiContext.userInfo())
                .setAddress(apiContext.ip())
                .setTimestamp(apiContext.timestamp());
        try {
            filterChain.doFilter(request, response);
        } finally {
            logContext(context, request);
            OperationContextHolder.remove();
        }
    }

    private void logContext(OperationContext context, HttpServletRequest request) {
        Operation operation = context.build();
        if (operation.operator() == null || operation.systemResource() == null) {
            return;
        }
        if (operation.operateType() == null) {
            logger.error("Not configured operation type, skip logging. Source: {}.",
                    request.getRequestURI());
            return;
        }
        operateLogger.recordOperate(operation);
    }
}
