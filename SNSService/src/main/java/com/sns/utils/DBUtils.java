package com.sns.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mysql db CRUD
 * @author fullpanic
 *
 */
public class DBUtils {
    /**
     * sqls
     */
    public static final String SQL_USER_INSERT = "insert into tb_users(uid,pwd,status) values(?,?,?)";
    
    public static final String SQL_USER_AUTH = "select 1 from tb_users where uid=? and pwd=?";
    
    public static final String SQL_USER_UPDATE_PWD = "update tb_users set pwd=? where uid=? and pwd=?";
    
    public static final String SQL_USER_DELETED = "update tb_users set status=-1 where uid=?";
    
    public static final String SQL_USER_QUERY_BYID = "select * from tb_users where uid=?";
    
    private static Logger logger = LoggerFactory.getLogger(DBUtils.class);
    
    /**
     * close crud 
     * @param rs
     * @param ps
     */
    public static void close(ResultSet rs, PreparedStatement ps, Connection connection) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            logger.error("close connection failed:", e);
        }
    }
    
    /**
     * check exists
     * @param sql
     * @return
     */
    public static boolean isExists(String sql, Object... values) {
        LogWriter.debugLog(logger, sql, values);
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean status = false;
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            ps = connection.prepareStatement(sql);
            if (values != null && values.length > 0) {
                int i = 1;
                for (Object p : values) {
                    ps.setObject(i++, p);
                }
            }
            rs = ps.executeQuery();
            status = rs.next();
        }
        catch (Exception e) {
            LogWriter.errorLog(logger, e, sql, values);
        }
        finally {
            close(rs, ps, connection);
        }
        return status;
    }
    
    /**
     * insert data to mysql
     * @param sql insert sql
     */
    public static int upsert(String sql, Object... values) {
        LogWriter.debugLog(logger, sql, values);
        PreparedStatement ps = null;
        int rs = 0;
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            ps = connection.prepareStatement(sql);
            if (values != null && values.length > 0) {
                int i = 1;
                for (Object p : values) {
                    ps.setObject(i++, p);
                }
            }
            rs = ps.executeUpdate();
        }
        catch (Exception e) {
            LogWriter.errorLog(logger, e, sql, values);
        }
        finally {
            close(null, ps, connection);
        }
        return rs;
    }
    
    /**
     * query fro tuple
     * @param sql
     * @param values
     * @return key is Uppercase
     */
    public static List<Map<String, Object>> query(String sql, Object... values) {
        LogWriter.debugLog(logger, sql, values);
        PreparedStatement ps = null;
        Connection connection = null;
        ResultSet resultSet = null;
        List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
        try {
            connection = ConnectionPool.getConnection();
            ps = connection.prepareStatement(sql);
            if (values != null && values.length > 0) {
                int i = 1;
                for (Object p : values) {
                    ps.setObject(i++, p);
                }
            }
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                int size = resultSet.getMetaData().getColumnCount();
                Map<String, Object> map = new HashMap<String, Object>(size);
                for (int i = 1; i < size + 1; i++) {
                    String key = resultSet.getMetaData().getColumnName(i).toUpperCase();
                    map.put(key, resultSet.getObject(i));
                }
                //add
                rs.add(map);
            }
        }
        catch (Exception e) {
            LogWriter.errorLog(logger, e, sql, values);
        }
        finally {
            close(resultSet, ps, connection);
        }
        return rs;
    }
    
    /**
     * execute as atomic business
     * @param sql
     * @param values
     * @return
     */
    public static boolean execute(Connection connection, String sql, Object... values) {
        LogWriter.debugLog(logger, sql, values);
        PreparedStatement ps = null;
        boolean rs = false;
        try {
            ps = connection.prepareStatement(sql);
            if (values != null && values.length > 0) {
                int i = 1;
                for (Object p : values) {
                    ps.setObject(i++, p);
                }
            }
            rs = ps.execute();
        }
        catch (Exception e) {
            LogWriter.errorLog(logger, e, sql, values);
        }
        finally {
            close(null, ps, null);
        }
        return rs;
    }
    
}
