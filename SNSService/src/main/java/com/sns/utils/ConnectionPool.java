package com.sns.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * connection pool based on apaceh dbcp
 * @author fullpanic
 *
 */
public class ConnectionPool {
    
    private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);
    
    private static BasicDataSource dataSource = null;
    
    /**
     * init from file
     */
    public static void init(String configPath) {
        if (dataSource != null) {
            try {
                dataSource.close();
            }
            catch (Exception e) {
            }
            dataSource = null;
        }
        try {
            Properties p = loadFromFile(configPath);
            dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
            logger.debug("init db:" + p);
        }
        catch (Exception e) {
            logger.error("init db pool failed:", e);
            System.exit(-1);
        }
    }
    
    /**
     * return pool
     * @return
     */
    public static BasicDataSource getDataSource() {
        return dataSource;
    }
    
    /**
     * get connection
     * @return
     * @throws SQLException
     */
    public static Connection getConnection()
        throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        return null;
    }
    
    private static Properties loadFromFile(String path)
        throws Exception {
        Properties p = new Properties();
        List<String> lines = IOUtils.readLines(System.class.getResourceAsStream(path));
        for (String line : lines) {
            //skip
            if (!StringUtils.startsWith(line, "#")) {
                String[] kv = line.split("=");
                if (kv != null && kv.length == 2) {
                    p.put(kv[0], kv[1]);
                }
            }
        }
        return p;
    }
}
