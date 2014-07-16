package com.sns.utils;

import java.util.Arrays;

import org.slf4j.Logger;

/**
 * write log with log level
 * @author fullpanic
 *
 */
public class LogWriter {
    /**
     * write sql debug
     * @param logger
     * @param sql
     * @param objects
     */
    public static void debugLog(Logger logger, String sql, Object... objects) {
        if (logger.isDebugEnabled()) {
            logger.debug("execute sql[" + sql + "], params:" + Arrays.asList(objects));
        }
    }
    
    /**
     * write normal debug info
     * @param logger
     * @param debug
     */
    public static void debugLog(Logger logger, String debug) {
        if (logger.isDebugEnabled()) {
            logger.debug(debug);
        }
    }
    
    /**
     * write sql error
     * @param logger
     * @param e
     * @param sql
     * @param objects
     */
    public static void errorLog(Logger logger, Exception e, String sql, Object... objects) {
        logger.error("faile to execute sql[" + sql + "], params:" + Arrays.asList(objects), e);
    }
}
