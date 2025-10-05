package com.epam.rd.autocode.spring.project.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(reg -> reg
            .requestMatchers(HttpMethod.GET, "/api/books/**").permitAll()
            .requestMatchers("/api/books/**").hasRole("EMPLOYEE")
            .requestMatchers(HttpMethod.GET, "/api/clients/**").hasRole("EMPLOYEE")
            .requestMatchers("/api/orders/employee/**").hasRole("EMPLOYEE")
            .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("CLIENT")
            .requestMatchers("/api/orders/client/**").hasRole("CLIENT")
            .anyRequest().authenticated()
        );
        http.httpBasic(b -> {});
        http.authenticationProvider(daoAuthProvider());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        //TODO change to BCrypt
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }
}
