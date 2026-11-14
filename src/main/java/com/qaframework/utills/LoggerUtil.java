package com.qaframework.utills;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized logger utility for the framework.
 * Wraps SLF4J's LoggerFactory (backed by Log4j2) so logger creation
 * and common logging patterns are consistent across all classes.
 */
public class LoggerUtil {

    private LoggerUtil() {
        // utility class - no instances
    }
    
    /**
     * Returns a Logger instance scoped to the calling class.
     * Usage: private static final Logger log = LoggerUtil.getLogger(LoginPage.class);
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Returns a Logger instance scoped to the calling class.
     * Usage: private static final Logger log = LoggerUtil.getLogger(LoginPage.class);
     */
//    public static Logger getLogger(Class<?> clazz) {
//        return LoggerFactory.getLogger(clazz);
//    }

    /**
     * Logs a readable test step - useful for tracing execution flow
     * in reports/CI logs.
     */
    public static void logStep(Logger log, String stepDescription) {
        log.info("STEP: {}", stepDescription);
    }

    /**
     * Logs an error along with the exception stack trace.
     */
//    public static void logError(Logger log, String message, Throwable t) {
//        log.error("ERROR: {}", message, t);
//    }
}