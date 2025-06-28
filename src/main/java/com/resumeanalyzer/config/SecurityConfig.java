package com.resumeanalyzer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy; // For stateless API

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection. This is common for stateless REST APIs.
                // For production, consider enabling CSRF or using token-based security properly.
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {}) // Enable CORS
                // Configure authorization for HTTP requests
                .authorizeHttpRequests(authorize -> authorize
                        // Permit all requests to /api/** without authentication.
                        // This is the key change to allow your Angular frontend to connect.
                        // Ensure your actual backend endpoints start with /api/
                        .requestMatchers("/api/**").permitAll()
                        // Any other request (if you had other non-/api endpoints) would require authentication.
                        .anyRequest().authenticated()
                )
                // Configure session management to be stateless, as is typical for REST APIs.
                // This ensures that sessions are not created or used to store security context.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    // You might also need to define a PasswordEncoder bean if you plan to use user accounts
    // and store hashed passwords. For simple API access in development, it might not be strictly necessary
    // for this specific 'permitAll' configuration, but it's good practice for future expansion.
    /*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    */
}
