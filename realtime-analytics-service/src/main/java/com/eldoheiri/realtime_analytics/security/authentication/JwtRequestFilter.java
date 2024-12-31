package com.eldoheiri.realtime_analytics.security.authentication;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eldoheiri.realtime_analytics.dataobjects.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public abstract class JwtRequestFilter extends OncePerRequestFilter {

    private JWTUtil jwtUtil;

    public JwtRequestFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new BadCredentialsException("Invalid Authorization header");
            }
            String jwt = authorizationHeader.substring(7);

            String subject = jwtUtil.validateTokenAndExtractSubject(jwt);
            if (subject == null) {
                throw new BadCredentialsException("Invalid Authorization token");
            }
            if (!validateTokenSubject(subject, request)) {
                throw new JwtException("Invalid token");
            }
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(subject, null, List.of());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage(e.getMessage());
            errorResponse.setCode(401);

            try {
                response.setStatus(401);
                response.getWriter().write(convertyObjectToJson(errorResponse));
            } catch (Exception exception) {
                exception.printStackTrace();
                response.setStatus(500);
                response.getWriter().write("Internal Server Error");
            }
            
            return;
        }
    }

    private String convertyObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }

    abstract public boolean validateTokenSubject(String tokenSubject, HttpServletRequest request) throws JwtException;
    
}
