package com.desafio.forohub.infra.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.authorization.SingleResultAuthorizationManager.permitAll;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    private SecurityFilter securityFilter;
    @Bean

    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement (session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests( authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(HttpMethod.POST,"/Login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers (  "/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean

    public AuthenticationManager authenticationManager (AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean

    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
