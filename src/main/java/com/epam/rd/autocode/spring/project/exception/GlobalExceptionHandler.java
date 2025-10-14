package com.epam.rd.autocode.spring.project.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public String handleInvalidCredentials(AuthenticationException ex, RedirectAttributes ra) {
        log.warn("auth.login.failure reason={}", ex.getClass().getSimpleName());
        ra.addFlashAttribute("loginError", "Невірний email або пароль");
        return "redirect:/login?error";
    }

    @ExceptionHandler(AccountStatusException.class)
    public String handleAccountStatus(AccountStatusException ex, RedirectAttributes ra) {
        log.warn("auth.login.account_status reason={}", ex.getClass().getSimpleName());
        ra.addFlashAttribute("loginError", "Обліковий запис недоступний");
        return "redirect:/login?error";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException ex, Model model, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.NOT_FOUND.value());
        log.warn("not.found message={}", ex.getMessage());
        model.addAttribute("status", HttpStatus.NOT_FOUND.value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(AlreadyExistException.class)
    public String handleAlreadyExist(AlreadyExistException ex, Model model, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.CONFLICT.value());
        log.warn("already.exist message={}", ex.getMessage());
        model.addAttribute("status", HttpStatus.CONFLICT.value());
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation(MethodArgumentNotValidException ex, Model model, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.BAD_REQUEST.value());
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError e : ex.getBindingResult().getFieldErrors()) {
            errors.put(e.getField(), e.getDefaultMessage());
        }
        log.warn("validation.error fields={}", errors.keySet());
        model.addAttribute("status", HttpStatus.BAD_REQUEST.value());
        model.addAttribute("message", "Некоректні дані");
        model.addAttribute("errors", errors);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAny(Exception ex, Model model, HttpServletResponse resp) {
        resp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        log.error("unhandled.error type={} message={}", ex.getClass().getSimpleName(), ex.getMessage());
        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        model.addAttribute("message", "Внутрішня помилка");
        return "error";
    }
}
