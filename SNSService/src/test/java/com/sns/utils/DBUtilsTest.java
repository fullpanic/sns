package com.sns.utils;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sns.resource.consts.DBSQLs;
import com.sns.utils.DBUtils.SQL_OPS;

public class DBUtilsTest {
    
    @Before
    public void init() {
        ConnectionPool.init("/conf/Datasource.properties");
    }
    
    @Test
    public void testInsert()
        throws Exception {
        String uid = "test";
        String pwd = "123";
        int rs = DBUtils.upsert(DBSQLs.SQL_USER_INSERT, uid, pwd, 0);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testUpdate()
        throws Exception {
        String uid = "lwying";
        String newpwd = "123";
        String pwd = "789";
        int rs = DBUtils.upsert(DBSQLs.SQL_USER_UPDATE_PWD, newpwd, uid, pwd);
        System.out.println(rs);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testDelete()
        throws Exception {
        String uid = "test";
        int rs = DBUtils.upsert(DBSQLs.SQL_USER_DELETED, uid);
        Assert.assertTrue(rs > 0);
    }
    
    @Test
    public void testQuery()
        throws Exception {
        List<Map<String, Object>> rs = DBUtils.query(DBSQLs.SQL_USER_QUERY_BYID, 1);
        Assert.assertTrue(rs.size() > 0);
        System.out.println(rs);
    }
    
    @Test
    public void testAtmoic()
        throws Exception {
        Connection connection = ConnectionPool.getConnection();
        String uid = "lwying";
        String pwd = "123";
        List<Map<String, Object>> rs = DBUtils.query(DBSQLs.SQL_USER_QUERY_BYID, uid);
        String old = (String)rs.get(0).get("PWD");
        //insert.
        try {
            //update
            DBUtils.execute(connection, DBSQLs.SQL_USER_UPDATE_PWD, "new", uid, pwd);
            //throw error
            DBUtils.execute(connection, DBSQLs.SQL_USER_INSERT, uid, pwd);
            connection.commit();
        }
        catch (Exception e) {
            // TODO: handle exception
        }
        finally {
            DBUtils.close(null, null, connection);
        }
        //query
        List<Map<String, Object>> rs1 = DBUtils.query(DBSQLs.SQL_USER_QUERY_BYID, uid);
        String old1 = (String)rs1.get(0).get("PWD");
        Assert.assertTrue(StringUtils.equals(old, old1));
    }
    
    @Test
    public void testExists()
        throws Exception {
        String sql = "select 1 from tb_users where uid='test'";
        boolean rs = DBUtils.isExists(sql);
        Assert.assertTrue(rs);
    }
    
    @Test
    public void testSQLs() {
        String tableName = "test";
        
        String[] keys = new String[] {"name", "pwd"};
        String[] cons = new String[] {"id", "status"};
        
        SQL_OPS ops = SQL_OPS.SELECT;
        String sql = DBUtils.createSQL(tableName, ops, keys, cons);
        String rs = "select name,pwd from test where id=? and status=?";
        System.err.println(sql);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(sql, rs));
        
        //insert
        ops = SQL_OPS.INSERT;
        sql = DBUtils.createSQL(tableName, ops, keys, cons);
        rs = "insert into test(name,pwd) values(?,?)";
        System.err.println(sql);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(sql, rs));
        
        //update
        ops = SQL_OPS.UPDATE;
        sql = DBUtils.createSQL(tableName, ops, keys, cons);
        rs = "update test set name=?,pwd=? where id=? and status=?";
        System.err.println(sql);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(sql, rs));
        
        //delete
        ops = SQL_OPS.DELETE;
        sql = DBUtils.createSQL(tableName, ops, null, cons);
        rs = "delete from test where id=? and status=?";
        System.err.println(sql);
        Assert.assertTrue(StringUtils.equalsIgnoreCase(sql, rs));
    }
    
}
