package com.infinityworks.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A logger that lazily invokes a supplier IFF the log level is enabled.
 * Helps avoid construction of log messages that are not needed.
 */
public class LambdaLogger {
    private final Logger logger;

    public static <T> LambdaLogger getLogger(Class<T> loggerClass) {
        return new LambdaLogger(LoggerFactory.getLogger(loggerClass));
    }

    public void info(Supplier<String> supplier) {
        if (logger.isInfoEnabled()) {
            logger.info(supplier.get());
        }
    }

    public void debug(Supplier<String> supplier) {
        if (logger.isDebugEnabled()) {
            logger.debug(supplier.get());
        }
    }

    public void warn(Supplier<String> supplier) {
        if (logger.isWarnEnabled()) {
            logger.warn(supplier.get());
        }
    }

    public void error(Supplier<String> supplier) {
        if (logger.isErrorEnabled()) {
            logger.error(supplier.get());
        }
    }

    public void trace(Supplier<String> supplier) {
        if (logger.isTraceEnabled()) {
            logger.trace(supplier.get());
        }
    }

    public LambdaLogger(Logger logger) {
        this.logger = requireNonNull(logger, "logger cannot be null");
    }
}