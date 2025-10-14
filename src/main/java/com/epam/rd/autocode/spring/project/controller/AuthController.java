package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.security.JwtAuthFilter;
import com.epam.rd.autocode.spring.project.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpServletResponse response) {
        log.info("auth.login request email={}", email);
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password));

        var user = userDetailsService.loadUserByUsername(email);
        String token = jwtService.generateAccessToken(user);

        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.ACCESS_COOKIE, token)
            .httpOnly(true)
            .secure(false)
            .sameSite("Lax")
            .path("/")
            .maxAge( jwtExpirationSeconds(token) )
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        log.info("auth.login.success email={}", email);

        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.ACCESS_COOKIE, "")
            .httpOnly(true).secure(false).sameSite("Lax")
            .path("/")
            .maxAge(0)
            .build();
        response.addHeader("Set-Cookie", cookie.toString());
        log.info("auth.logout.done");
        return "redirect:/login?logout";
    }

    private long jwtExpirationSeconds(String token) {
        return 1800;
    }
}
