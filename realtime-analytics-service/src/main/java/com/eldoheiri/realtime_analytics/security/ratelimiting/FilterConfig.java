package com.eldoheiri.realtime_analytics.security.ratelimiting;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    FilterRegistrationBean<RateLimitRequetFilter> registerRateLimitingFilter() {
        FilterRegistrationBean<RateLimitRequetFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new RateLimitRequetFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
