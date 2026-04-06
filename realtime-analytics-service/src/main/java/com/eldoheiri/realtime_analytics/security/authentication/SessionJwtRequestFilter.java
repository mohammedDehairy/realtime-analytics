package com.eldoheiri.realtime_analytics.security.authentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

public class SessionJwtRequestFilter extends JwtRequestFilter {

    public SessionJwtRequestFilter(JWTUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    public boolean validateTokenSubject(Claims claims, HttpServletRequest request) throws JwtException {
        String[] pathComponents = request.getRequestURI().split("/");
        if (pathComponents.length < 6) {
            throw new JwtException("Invalid request path");
        }
        String applicationId = pathComponents[4];
        String sessionId = pathComponents[pathComponents.length - 2];
        return sessionId.equals(claims.getSubject())
            && applicationId.equals(claims.get("applicationId", String.class));
    }
    
}
