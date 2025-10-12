package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.service.OrderService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    private final OrderService orderService;
    private final ClientService clientService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public String myOrders(Model model, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        model.addAttribute("orders", orderService.getOrdersByClient(client.getEmail()));
        return "orders";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        var cart = orderService.getCart(client.getId());
        model.addAttribute("cartItems", cart.getItems());
        model.addAttribute("total", cart.getTotal());
        return "cart";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/add")
    public String addToCart(@RequestParam Long bookId, @RequestParam(defaultValue = "1") Integer quantity, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        orderService.addToCart(client.getId(), bookId, quantity);
        return "redirect:/orders/cart";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/update")
    public String updateItem(@RequestParam Long itemId, @RequestParam Integer quantity, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        orderService.updateCartItem(client.getId(), itemId, quantity);
        return "redirect:/orders/cart";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam Long itemId, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        orderService.removeCartItem(client.getId(), itemId);
        return "redirect:/orders/cart";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/checkout")
    public String checkout(Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        orderService.checkout(client.getId());
        return "redirect:/orders?created";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/manage")
    public String manage(Model model) {
        model.addAttribute("orders", orderService.findAllNew());
        return "orders-manage";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id) {
        orderService.confirm(id);
        return "redirect:/orders/manage?confirmed";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return "redirect:/orders/manage?canceled";
    }
}
