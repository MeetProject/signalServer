package com.meetProject.signalserver.util;

import java.security.Principal;

public class WebSocketUtils {
    public static String getUserId(Principal principal) {
        if(principal == null) {
            throw new NullPointerException("Principal is null");
        }
        return principal.getName();
    }
}
