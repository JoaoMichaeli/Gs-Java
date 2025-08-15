package com.gs.EcoDenuncia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final AuthFilter authFilter;

    public SecurityConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/swagger-ui/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                        .requestMatchers("/users/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/city/**").permitAll()
                        .requestMatchers("/city/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/complaints/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/complaints/**").hasRole("ADMIN")
                        .requestMatchers("/complaints/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/location/**").permitAll()
                        .requestMatchers("/location/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/neighborhood/**").permitAll()
                        .requestMatchers("/neighborhood/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/followup/denuncia/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/followup/**").hasRole("ADMIN")
                        .requestMatchers("/followup/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/state/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/state/**").permitAll()
                        .requestMatchers("/state/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/organizations/**").permitAll()
                        .requestMatchers("/organizations/**").hasRole("ADMIN")
                )
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
