package com.sns.resource.consts;

/**
 * define project constant values
 * @author fullpanic
 *
 */
public class AppConsts {
    
    /**
     * sys encoding
     */
    public static final String SYS_ENCODING = "UTF-8";
    
    public static final String BLANK = " ";
    
    public static final String UID_PATTERN = "[\\w|_]+";
    
    /**
     * user constant
     * @author fullpanic
     *
     */
    public static class UserKeys {
        public static final String UID = "uid";
        
        /**
         * user name 
         */
        public static final String USERNAME = "username";
        
        /**
         * user password
         */
        public static final String PASSWORD = "password";
        
        /**
         * user token
         */
        public static final String TOKEN = "token";
        
        /**
         * user status
         */
        public static final String STATUS = "status";
        
        /**
         * user captcha
         */
        public static final String CAPTCHA = "captcha";
    }
    
}
