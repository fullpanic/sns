package com.sns.resource.consts;

/**
 * db ops sql consts
 * @author fullpanic
 *
 */
public class DBSQLs {
    /**
     * insert new user
     */
    public static final String SQL_USER_INSERT =
        "insert into tb_userregist(username,password,createtime) values(?,?,null)";
    
    /**
     * insert new captcha
     */
    public static final String SQL_USER_NEW_CAPTCHA = "insert into tb_userauth(uid,captcha,createtime)values(?,?,null)";
    
    /**
     * insert token 
     */
    public static final String SQL_USER_TOKEN_INSERT = "update tb_userauth set token=?,source=? where uid=?";
    
    /**
     * auth user by pwd and username
     */
    public static final String SQL_USER_AUTH = "select id from tb_userregist where username=? and password=?";
    
    /**
     * update user password
     */
    public static final String SQL_USER_UPDATE_PWD =
        "update tb_userregist set password=? where username=? and password=?";
    
    /**
     * 
     */
    public static final String SQL_USER_DELETED = "update tb_userinfo set status=-1 where username=? and password=?";
    
    /**
     * 
     */
    public static final String SQL_USER_QUERY_BYID = "select * from tb_userregist where id=?";
    
}
