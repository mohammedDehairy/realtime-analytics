package com.eldoheiri.realtime_analytics.security.authentication;

import java.io.IOException;

import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eldoheiri.realtime_analytics.dataobjects.error.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class APIKeyFilter extends OncePerRequestFilter {
    private final Environment environment;

    public APIKeyFilter(Environment environment) {
        this.environment = environment;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String apiKey = request.getHeader("x-api-key");
        String applicationId = resolveApplicationId(request);
        if (applicationId == null) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid API path");
            errorResponse.setCode(404);
            sendError(errorResponse, response);
            return;
        }

        String expectedApiKey = environment.getProperty("api.security.application-keys." + applicationId);
        if (expectedApiKey == null || !expectedApiKey.equals(apiKey)) {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setMessage("Invalid API key");
            errorResponse.setCode(401);
            sendError(errorResponse, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES));
        filterChain.doFilter(request, response);
    }

    private String resolveApplicationId(HttpServletRequest request) {
        String[] pathComponents = request.getRequestURI().split("/");
        if (pathComponents.length < 5) {
            return null;
        }

        return pathComponents[3];
    }

    private void sendError(ErrorResponse errorResponse, HttpServletResponse response) throws IOException {
        try {
            response.setStatus(errorResponse.getCode());
            response.setContentType("application/json");
            response.getWriter().write(convertyObjectToJson(errorResponse));
        } catch (Exception exception) {
            exception.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("Internal Server Error");
        }
    }

    private String convertyObjectToJson(Object object) throws JsonProcessingException {
        if (object == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}
