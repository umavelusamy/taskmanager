package com.taskmanager.taskmanagerapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true) // enables method level authorization/permission
public class SecurityConfiguration {

        private final JwtAuthenticationFilter jwtAuthFilter;

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorizeHttp -> {
                                        authorizeHttp.requestMatchers(AppConstants.PUBLIC_ENDPOINTS)
                                                        .permitAll();
                                        authorizeHttp.anyRequest().authenticated();
                                })
                                .sessionManagement(management -> management
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .logout(logout -> logout.disable())
                                .addFilterBefore(jwtAuthFilter, AuthorizationFilter.class);

                return http.build();
        }
}
