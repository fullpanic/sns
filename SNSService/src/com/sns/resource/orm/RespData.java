package com.sns.resource.orm;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;

/**
 * return json data to client
 * @author fullpanic
 *
 */
public class RespData {
    private static Logger logger = Logger.getLogger(RespData.class);
    
    private Status status;
    
    private JSONObject msg;
    
    private String errMsg;
    
    public RespData() {
    }
    
    public RespData(Status statusCode) {
        this.status = statusCode;
    }
    
    public RespData(Status statusCode, JSONObject content) {
        this.status = statusCode;
        this.msg = content;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public JSONObject getMsg() {
        return msg;
    }
    
    public void setMsg(JSONObject msg) {
        this.msg = msg;
    }
    
    public String getErrMsg() {
        return errMsg;
    }
    
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
    
    public void put(String key, Object value)
        throws JSONException {
        if (this.msg == null) {
            this.msg = new JSONObject();
        }
        this.msg.put(key, value);
    }
    
    /**
     * get response body
     * @return
     */
    public Representation getBody() {
        JSONObject object = new JSONObject();
        try {
            object.put("status", status.getCode());
            object.put("msg", msg);
            if (StringUtils.isNotEmpty(errMsg)) {
                object.put("error", errMsg);
            }
        }
        catch (Exception e) {
            logger.error("create response failed:", e);
        }
        return new JsonRepresentation(object);
    }
}
