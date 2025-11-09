package com.surest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.surest.api.service.SurestUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SurestUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final DelegatedAuthenticationEntryPoint authEntryPoint;

    public SecurityConfig(SurestUserDetailsService userDetailsService,
                          JwtRequestFilter jwtRequestFilter,
                          DelegatedAuthenticationEntryPoint authEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.authEntryPoint = authEntryPoint;
    }

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authProvider) throws Exception {
        http.cors().and()
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authProvider)
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html",
                    "/api/account/**", "/api/auth/**",
                    "/api/files/**"
                ).permitAll()
                .requestMatchers("/api/roles/**").permitAll()
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/create-member").hasAuthority("ADMIN")
                .requestMatchers("/api/update-member-by-id/**").hasAuthority("ADMIN")
                .requestMatchers("/api/delete-member-by-id/**").hasAuthority("ADMIN")
                .requestMatchers("/api/guest/**").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().authenticated()
            );

        return http.build();
    }
}


