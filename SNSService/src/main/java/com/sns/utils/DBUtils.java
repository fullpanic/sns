package com.sns.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sns.resource.consts.AppConsts;

/**
 * mysql db CRUD
 * @author fullpanic
 *
 */
public class DBUtils {
    private static Logger logger = LoggerFactory.getLogger(DBUtils.class);
    
    /**
     * sql operators
     * @author fullpanic
     *
     */
    public static enum SQL_OPS {
        SELECT("select"), INSERT("insert into"), UPDATE("update"), DELETE("delete from");
        
        private String ops_value;
        
        SQL_OPS(String v) {
            this.ops_value = v;
        }
        
        @Override
        public String toString() {
            return this.ops_value;
        }
    }
    
    /**
     * create sql
     * @param tableName
     * @param ops
     * @param keys
     * @return
     */
    public static String createSQL(String tableName, SQL_OPS ops, String[] keys, String[] conditions) {
        //check
        if (StringUtils.isEmpty(tableName) || ops == null) {
            logger.error("input args illegal!");
            return null;
        }
        //get keys
        StringBuilder ks = new StringBuilder();
        int len = keys == null ? 0 : keys.length;
        for (int i = 0; i < len; i++) {
            ks.append(keys[i]);
            if (i + 1 < len) {
                ks.append(",");
            }
        }
        //get conditions
        StringBuilder cs = new StringBuilder();
        len = conditions.length;
        for (int i = 0; i < len; i++) {
            cs.append(conditions[i]).append("=?");
            if (i + 1 < len) {
                cs.append(" and ");
            }
        }
        //create sql
        StringBuilder sql = new StringBuilder(ops.ops_value).append(AppConsts.BLANK);
        switch (ops) {
            case SELECT:
                sql.append(ks).append(" from ").append(tableName).append(" where ").append(cs);
                break;
            case INSERT:
                cs.setLength(0);
                len = keys.length;
                for (int i = 0; i < len; i++) {
                    cs.append("?");
                    if (i + 1 < len) {
                        cs.append(",");
                    }
                }
                sql.append(tableName).append("(").append(ks).append(") values(").append(cs).append(")");
                break;
            case UPDATE:
                ks.setLength(0);
                len = keys.length;
                for (int i = 0; i < len; i++) {
                    ks.append(keys[i]).append("=?");
                    if (i + 1 < len) {
                        ks.append(",");
                    }
                }
                sql.append(tableName).append(" set ").append(ks).append(" where ").append(cs);
                break;
            case DELETE:
                sql.append(tableName).append(" where ").append(cs);
                break;
            default:
                break;
        }
        
        return sql.toString().toUpperCase();
    }
    
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
     * @throws Exception 
     */
    public static boolean isExists(String sql, Object... values)
        throws Exception {
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
            throw e;
        }
        finally {
            close(rs, ps, connection);
        }
        return status;
    }
    
    /**
     * insert data to mysql
     * @param sql insert sql
     * @throws Exception 
     */
    public static int upsert(String sql, Object... values)
        throws Exception {
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
            throw e;
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
     * @throws Exception 
     */
    public static List<Map<String, Object>> query(String sql, Object... values)
        throws Exception {
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
            throw e;
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
     * @throws Exception 
     */
    public static boolean execute(Connection connection, String sql, Object... values)
        throws Exception {
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
            throw e;
        }
        finally {
            close(null, ps, null);
        }
        return rs;
    }
    
}
