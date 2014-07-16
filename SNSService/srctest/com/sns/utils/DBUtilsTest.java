package com.sns.utils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DBUtilsTest {
    
    @Before
    public void init() {
        ConnectionPool.init("/Datasource.properties");
    }
    
    @Test
    public void testInsert() {
        String uid = "test";
        String pwd = "123";
        int rs = DBUtils.upsert(DBUtils.SQL_USER_INSERT, uid, pwd, 0);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testUpdate() {
        String uid = "lwying";
        String newpwd = "123";
        String pwd = "789";
        int rs = DBUtils.upsert(DBUtils.SQL_USER_UPDATE_PWD, newpwd, uid, pwd);
        System.out.println(rs);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testDelete() {
        String uid = "test";
        int rs = DBUtils.upsert(DBUtils.SQL_USER_DELETED, uid);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testQuery() {
        String uid = "test";
        List<Map<String, Object>> rs = DBUtils.query(DBUtils.SQL_USER_QUERY_BYID, uid);
        Assert.assertTrue(rs.size() > 0);
        System.out.println(rs);
    }
    
    @Test
    public void testAtmoic()
        throws Exception {
        Connection connection = ConnectionPool.getConnection();
        String uid = "lwying";
        String pwd = "123";
        List<Map<String, Object>> rs = DBUtils.query(DBUtils.SQL_USER_QUERY_BYID, uid);
        String old = (String)rs.get(0).get("PWD");
        //insert.
        try {
            //update
            DBUtils.execute(connection, DBUtils.SQL_USER_UPDATE_PWD, "new", uid, pwd);
            //throw error
            DBUtils.execute(connection, DBUtils.SQL_USER_INSERT, uid, pwd);
            connection.commit();
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            DBUtils.close(null, null, connection);
        }
        //query
        List<Map<String, Object>> rs1 = DBUtils.query(DBUtils.SQL_USER_QUERY_BYID, uid);
        String old1 = (String)rs1.get(0).get("PWD");
        Assert.assertTrue(StringUtils.equals(old, old1));
    }
    
    @Test
    public void testExists() {
        String sql = "select 1 from tb_users where uid='test'";
        boolean rs = DBUtils.isExists(sql);
        Assert.assertTrue(rs);
    }
    
}
