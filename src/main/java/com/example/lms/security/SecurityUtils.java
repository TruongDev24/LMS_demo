package com.example.lms.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    public static CustomUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        CustomUserDetails details = getCurrentUserDetails();
        return details != null ? details.getId() : null;
    }
}
