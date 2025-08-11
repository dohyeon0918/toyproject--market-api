package com.example.market_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // REST API면 개발용으로 CSRF 끄기
                .authorizeHttpRequests(auth -> auth
                        // Swagger 열기
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // 상품 API도 임시 오픈 (원하면 좁혀도 됨)
                        .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/products/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/products/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults()) // 일단 Basic으로 테스트 가능
                .build();
    }
}