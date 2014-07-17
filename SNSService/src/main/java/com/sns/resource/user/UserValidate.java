package com.sns.resource.user;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.sns.resource.consts.UserStatus;
import com.sns.resource.orm.RespData;
import com.sns.utils.DBUtils;
import com.sns.utils.RequestUtils;
import com.sns.utils.TokenUtils;

/**
 * user validate by captcha
 * @author fullpanic
 *
 */
public class UserValidate extends ServerResource {
    private static Logger logger = LoggerFactory.getLogger(UserValidate.class);
    
    @Post
    @Get
    public Representation validate() {
        RespData data = new RespData();
        String errmsg = null;
        Status status = Status.SUCCESS_OK;
        try {
            Form form = getQuery();
            //get userinfo
            String uid = RequestUtils.get(form, UserKeys.UID);
            String pwd = RequestUtils.get(form, UserKeys.CAPTCHA);
            //check and auth
            String token = auth(uid, pwd);
            if (StringUtils.isEmpty(token)) {
                throw new Exception("login failed!");
            }
            else {
                data.put(UserKeys.STATUS, UserStatus.NORMAL);
                data.put(UserKeys.TOKEN, token);
            }
        }
        catch (SNSException e) {
            logger.error("user login failed:", e);
            status = Status.CLIENT_ERROR_BAD_REQUEST;
            errmsg = e.getMessage();
        }
        catch (Exception e) {
            logger.error("json failed:", e);
            status = Status.SERVER_ERROR_INTERNAL;
            errmsg = e.getMessage();
        }
        //return
        data.setErrMsg(errmsg);
        data.setStatus(status);
        return data.getBody();
    }
    
    /**
     * auth
     * @param uid
     * @param pwd
     * @return
     */
    private String auth(String uid, String pwd)
        throws Exception {
        String token = null;
        List<Map<String, Object>> rs = DBUtils.query(DBSQLs.SQL_USER_AUTH, uid, pwd);
        if (CollectionUtils.isNotEmpty(rs)) {
            //create token
            token = TokenUtils.createToken(uid);
        }
        return token;
    }
}
