package com.eldoheiri.realtime_analytics.security.authentication;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;

public class SessionJwtRequestFilter extends JwtRequestFilter {

    public SessionJwtRequestFilter(JWTUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    public boolean validateTokenSubject(String tokenSubject, HttpServletRequest request) throws JwtException {
        String[] pathComponents = request.getRequestURI().split("/");
        if (pathComponents.length < 2) {
            throw new JwtException("Invalid request path");
        }
        String sessionId = pathComponents[pathComponents.length - 2];
        return sessionId.equals(tokenSubject);
    }
    
}
