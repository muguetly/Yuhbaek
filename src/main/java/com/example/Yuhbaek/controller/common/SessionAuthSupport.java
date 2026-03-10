package com.example.Yuhbaek.controller.common;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public abstract class SessionAuthSupport {

    protected Long requireLogin(HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        if (userId == null) throw new IllegalStateException("LOGIN_REQUIRED");
        return userId;
    }

    protected ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "로그인이 필요합니다"));
    }
}
