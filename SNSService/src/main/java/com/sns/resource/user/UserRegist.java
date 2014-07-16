package com.sns.resource.user;

import java.sql.Connection;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sns.resource.SNSException;
import com.sns.resource.consts.AppConsts.User;
import com.sns.resource.consts.UserStatus;
import com.sns.resource.orm.RespData;
import com.sns.utils.ConnectionPool;
import com.sns.utils.DBUtils;
import com.sns.utils.RequestUtils;

/**
 * user register
 * @author fullpanic
 *
 */
public class UserRegist extends ServerResource {
    private static Logger logger = LoggerFactory.getLogger(UserRegist.class);
    
    @Post
    @Get
    public Representation regist() {
        RespData data = new RespData();
        String errmsg = null;
        Status status = Status.SUCCESS_OK;
        Connection connection = null;
        try {
            connection = ConnectionPool.getConnection();
            Form form = getQuery();
            //get userinfo
            String uid = RequestUtils.get(form, User.NAME);
            String pwd = RequestUtils.get(form, User.PASSWORD);
            //write to db
            Object[] values = new Object[] {uid, pwd};
            DBUtils.execute(connection, DBUtils.SQL_USER_INSERT, values);
            //send msg for validate TODO
            //get token
            data.put(User.STATUS, UserStatus.NEW);
            //close
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
}
