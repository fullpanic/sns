package com.sns.resource.consts;

/**
 * define user status
 * @author fullpanic
 *
 */
public enum UserStatus {
    
    DELETED(-1), NEWADDED(0), NORMAL(1), DISABLE(2);
    
    int code;
    
    UserStatus(int c) {
        this.code = c;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.code);
    }
}
