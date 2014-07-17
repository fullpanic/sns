package com.sns.resource.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.lang.RandomStringUtils;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sns.resource.SNSException;
import com.sns.resource.consts.AppConsts.UserKeys;
import com.sns.resource.consts.DBSQLs;
import com.sns.resource.consts.TokenSource;
import com.sns.resource.consts.UserStatus;
import com.sns.resource.orm.RespData;
import com.sns.utils.ConnectionPool;
import com.sns.utils.DBUtils;
import com.sns.utils.LogWriter;
import com.sns.utils.RequestUtils;
import com.sns.utils.TokenUtils;

/**
 * user register
 * @author fullpanic
 *
 */
public class UserRegist extends ServerResource {
    private static Logger logger = LoggerFactory.getLogger(UserRegist.class);
    
    private Connection connection = null;
    
    @Post
    @Get
    public Representation regist() {
        RespData data = new RespData();
        String errmsg = null;
        Status status = Status.SUCCESS_OK;
        try {
            connection = ConnectionPool.getConnection();
            Form form = getQuery();
            //get userinfo
            String username = RequestUtils.get(form, UserKeys.USERNAME);
            if (!RequestUtils.validateUid(username)) {
                throw new SNSException("username illegal! number|char|_");
            }
            String pwd = RequestUtils.get(form, UserKeys.PASSWORD);
            //write to db
            long uid = insertUser(username, pwd);
            //send msg for validate
            data.put(UserKeys.CAPTCHA, sendCaptcha(uid));
            data.put(UserKeys.TOKEN, genToken(username, uid));
            //close
            data.put(UserKeys.UID, uid);
            data.put(UserKeys.STATUS, UserStatus.NEWADDED);
            connection.commit();
        }
        catch (SNSException e) {
            logger.error("user regist failed:", e);
            status = Status.CLIENT_ERROR_BAD_REQUEST;
            errmsg = e.getMessage();
        }
        catch (Exception e) {
            logger.error("regist failed:", e);
            status = Status.SERVER_ERROR_INTERNAL;
            errmsg = e.getMessage();
        }
        finally {
            DBUtils.close(null, null, connection);
        }
        //return
        data.setErrMsg(errmsg);
        data.setStatus(status);
        return data.getBody();
    }
    
    private long insertUser(String username, String password)
        throws Exception {
        String sql = DBSQLs.SQL_USER_INSERT;
        Object[] values = new Object[] {username, password};
        LogWriter.debugLog(logger, sql, values);
        PreparedStatement ps = null;
        ResultSet rs = null;
        long uid = -1;
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (values != null && values.length > 0) {
                int i = 1;
                for (Object p : values) {
                    ps.setObject(i++, p);
                }
            }
            ps.execute();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                uid = rs.getLong(1);
            }
        }
        catch (Exception e) {
            LogWriter.errorLog(logger, e, sql, values);
            throw e;
        }
        finally {
            DBUtils.close(rs, ps, null);
        }
        return uid;
    }
    
    private String sendCaptcha(long uid)
        throws Exception {
        String rs = RandomStringUtils.randomAlphabetic(4);
        //send as msg
        //TODO
        //write to db
        DBUtils.execute(connection, DBSQLs.SQL_USER_NEW_CAPTCHA, uid, rs);
        return rs;
    }
    
    private String genToken(String username, long uid)
        throws Exception {
        String token = TokenUtils.createToken(username);
        //write to db
        DBUtils.execute(connection, DBSQLs.SQL_USER_TOKEN_INSERT, token, TokenSource.SYSTEM, uid);
        return token;
    }
}
