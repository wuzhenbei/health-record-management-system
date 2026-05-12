package com.health.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static LoginUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof LoginUser loginUser) {
            return loginUser;
        }
        return null;
    }

    public static Long getCurrentUserId() {
        LoginUser user = getLoginUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getCurrentRole() {
        LoginUser user = getLoginUser();
        return user != null ? user.getRole() : null;
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(getCurrentRole());
    }

    public static boolean isDoctor() {
        return "DOCTOR".equals(getCurrentRole());
    }

    public static boolean isResident() {
        return "RESIDENT".equals(getCurrentRole());
    }
}
