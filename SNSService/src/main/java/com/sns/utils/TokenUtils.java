package com.sns.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * create and mange user's token for auth
 * @author fullpanic
 *
 */
public class TokenUtils {
    private static Logger logger = LoggerFactory.getLogger(TokenUtils.class);
    
    /**
     * create token<br>
     * md5(uid,system.time)
     * @param uid
     * @throws Exception 
     */
    public static String createToken(String uid)
        throws Exception {
        String token = null;
        long time = System.currentTimeMillis();
        byte[] bs = DigestUtils.md5(uid + time);
        try {
            token = DigestUtils.md5Hex(bs);
            LogWriter.debugLog(logger, uid + " create new token:" + token);
        }
        catch (Exception e) {
            logger.error("create token failed:", e);
            throw e;
        }
        return token;
    }
}
