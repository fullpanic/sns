package com.sns.utils;

import org.apache.commons.lang.StringUtils;
import org.restlet.data.Form;

import com.sns.resource.SNSException;

/**
 * request params parser
 * @author fullpanic
 *
 */
public class RequestUtils {
    /**
     * get param value by key
     * @param request request Reference 
     * @param key
     * @return null if not exist
     * @throws SNSException 
     */
    public static String get(Form form, String key)
        throws SNSException {
        if (form == null) {
            return null;
        }
        String rs = null;
        rs = form.getFirstValue(key);
        if (StringUtils.isEmpty(rs)) {
            throw new SNSException(key + " is illegal!");
        }
        return rs;
    }
}
