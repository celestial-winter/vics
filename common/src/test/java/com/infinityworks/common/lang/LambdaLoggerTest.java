package com.infinityworks.common.lang;

import com.infinityworks.common.logging.LambdaLogger;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Mockito.*;

public class LambdaLoggerTest {

    private LambdaLogger logger;
    private Logger backingLogger;

    @Before
    public void setUp() throws Exception {
        backingLogger = mock(Logger.class);
        logger = new LambdaLogger(backingLogger);
    }

    @Test
    public void shouldLogWarningWhenEnabled() throws Exception {
        when(backingLogger.isWarnEnabled()).thenReturn(true);
        logger.warn(() -> "Warning");
        verify(backingLogger, times(1)).warn("Warning");
    }

    @Test
    public void shouldNotLogWarningWhenDisabled() throws Exception {
        when(backingLogger.isWarnEnabled()).thenReturn(false);
        logger.warn(() -> "Warning");
        verify(backingLogger, times(0)).warn(anyString());
    }

    @Test
    public void shouldLogDebugWhenEnabled() throws Exception {
        when(backingLogger.isDebugEnabled()).thenReturn(true);
        logger.debug(() -> "Debug");
        verify(backingLogger, times(1)).debug("Debug");
    }

    @Test
    public void shouldNotLogDebugWhenDisabled() throws Exception {
        when(backingLogger.isDebugEnabled()).thenReturn(false);
        logger.warn(() -> "Debug");
        verify(backingLogger, times(0)).warn(anyString());
    }

    @Test
    public void shouldLogInfoWhenEnabled() throws Exception {
        when(backingLogger.isInfoEnabled()).thenReturn(true);
        logger.info(() -> "Info");
        verify(backingLogger, times(1)).info("Info");
    }

    @Test
    public void shouldNotLogInfoWhenDisabled() throws Exception {
        when(backingLogger.isInfoEnabled()).thenReturn(false);
        logger.info(() -> "Info");
        verify(backingLogger, times(0)).info(anyString());
    }

    @Test
    public void shouldLogErrorWhenEnabled() throws Exception {
        when(backingLogger.isErrorEnabled()).thenReturn(true);
        logger.error(() -> "Error");
        verify(backingLogger, times(1)).error("Error");
    }

    @Test
    public void shouldNotLogErrorWhenDisabled() throws Exception {
        when(backingLogger.isErrorEnabled()).thenReturn(false);
        logger.error(() -> "Error");
        verify(backingLogger, times(0)).error(anyString());
    }

    @Test
    public void shouldLogTraceWhenEnabled() throws Exception {
        when(backingLogger.isTraceEnabled()).thenReturn(true);
        logger.trace(() -> "Trace");
        verify(backingLogger, times(1)).trace("Trace");
    }

    @Test
    public void shouldNotLogTraceWhenDisabled() throws Exception {
        when(backingLogger.isTraceEnabled()).thenReturn(false);
        logger.trace(() -> "Trace");
        verify(backingLogger, times(0)).trace(anyString());
    }
}