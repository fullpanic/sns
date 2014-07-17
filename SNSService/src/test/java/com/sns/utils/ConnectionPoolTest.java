package com.sns.utils;

import java.sql.Connection;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConnectionPoolTest {
    
    @Before
    public void init() {
        ConnectionPool.init("/conf/Datasource.properties");
    }
    
    @Test
    public void test()
        throws Exception {
        Connection connection = ConnectionPool.getConnection();
        Assert.assertTrue(connection != null);
        Assert.assertTrue(connection.getAutoCommit() == false);
        BasicDataSource pool = ConnectionPool.getDataSource();
        Assert.assertTrue(pool.getMaxActive() == 30);
    }
    
}
