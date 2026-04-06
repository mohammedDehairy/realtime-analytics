package com.eldoheiri.realtime_analytics.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

public class AdminJwtRequestFilter extends JwtRequestFilter {

    public AdminJwtRequestFilter(JWTUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    public boolean validateTokenSubject(Claims claims, HttpServletRequest request) throws JwtException {
        return "admin".equals(claims.getSubject());
    }
}
