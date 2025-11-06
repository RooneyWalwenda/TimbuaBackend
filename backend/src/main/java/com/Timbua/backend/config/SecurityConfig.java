
package com.Timbua.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {  // Renamed to SecurityConfig

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow public access to Swagger/OpenAPI endpoints
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**",
                                "/configuration/**"
                        ).permitAll()
                        // Allow public access to your API endpoints (optional)
                        .requestMatchers("/api/**").permitAll()
                        // Require authentication for all other requests
                        .anyRequest().authenticated()
                )
                // Disable CSRF for API testing (optional)
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}