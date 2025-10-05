package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client/me")
    public List<OrderDTO> getMyOrders(Authentication auth) {
        return orderService.getOrdersByClient(auth.getName());
    }

    @PreAuthorize("hasRole('EMPLOYEE') or #email == authentication.name")
    @GetMapping("/client/{email}")
    public List<OrderDTO> getOrdersByClient(@PathVariable String email) {
        return orderService.getOrdersByClient(email);
    }

    @GetMapping("/employee/{email}")
    public List<OrderDTO> getOrdersByEmployee(@PathVariable String email) {
        return orderService.getOrdersByEmployee(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO addOrder(@Valid @RequestBody OrderDTO dto) {
        return orderService.addOrder(dto);
    }
}
