package com.eldoheiri.realtime_analytics.security.authentication;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

public class AdminJwtRequestFilter extends JwtRequestFilter {

    public AdminJwtRequestFilter(JWTUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    public boolean validateTokenSubject(String tokenSubject, HttpServletRequest request) throws JwtException {
        return "admin".equals(tokenSubject);
    }
}
