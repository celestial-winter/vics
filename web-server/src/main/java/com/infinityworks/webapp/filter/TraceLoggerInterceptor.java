package com.infinityworks.webapp.filter;

import com.infinityworks.webapp.common.LambdaLogger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Intercepts controller request response calls and records the time taken per request.
 */
public class TraceLoggerInterceptor extends HandlerInterceptorAdapter {
    private static final LambdaLogger log = LambdaLogger.getLogger(TraceLoggerInterceptor.class);

    private static final String START_LOG_TEMPLATE = "Canvass Request[%s] %s.";
    private static final String END_LOG_TEMPLATE = "Canvass Response[%s] %s. response_time=%s";

    private static final String CORRELATION_KEY = "correlationKey";
    private static final String START_TIME_KEY = "startTime";

    /**
     * Record the start time of the method invocation with a correlation key
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        log.trace(() -> {
            long startTime = System.currentTimeMillis();
            UUID correlationKey = UUID.randomUUID();
            request.setAttribute(START_TIME_KEY, startTime);
            request.setAttribute(CORRELATION_KEY, correlationKey);
            return String.format(START_LOG_TEMPLATE, correlationKey, request.getRequestURL().toString());
        });
        return true;
    }

    /**
     * Record the time taken
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.trace(() -> {
            long startTime = (Long) request.getAttribute(START_TIME_KEY);
            long endTime = System.currentTimeMillis();
            long timeTaken = endTime - startTime;
            UUID correlationKey = (UUID) request.getAttribute(CORRELATION_KEY);
            return String.format(END_LOG_TEMPLATE, correlationKey, request.getRequestURL().toString(), timeTaken);
        });
    }
}
