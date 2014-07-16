package com.sns.resource;

/**
 * app exceptions
 * @author fullpanic
 *
 */
public class SNSException extends Exception {
    
    /**
     * uuid
     */
    private static final long serialVersionUID = -8332703144443177405L;
    
    public SNSException(String msg) {
        super(msg);
    }
}
