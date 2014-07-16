package com.sns.utils;

import org.junit.Test;

public class TokenUtilsTest {
    
    @Test
    public void test()
        throws Exception {
        String uid = "test";
        String token = TokenUtils.createToken(uid);
        System.err.println(token);
    }
    
}
