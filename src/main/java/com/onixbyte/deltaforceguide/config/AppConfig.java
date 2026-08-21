package com.onixbyte.deltaforceguide.config;

import com.onixbyte.deltaforceguide.interceptor.TrafficInterceptor;
import com.onixbyte.deltaforceguide.properties.AppProperties;
import com.onixbyte.deltaforceguide.security.resolver.CurrentUserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfig implements WebMvcConfigurer {

    private final TrafficInterceptor trafficInterceptor;


    private final CurrentUserArgumentResolver currentUserArgumentResolver;

    @Autowired
    public AppConfig(
            TrafficInterceptor trafficInterceptor,
            CurrentUserArgumentResolver currentUserArgumentResolver
    ) {
        this.trafficInterceptor = trafficInterceptor;
        this.currentUserArgumentResolver = currentUserArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(trafficInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserArgumentResolver);
    }
}
