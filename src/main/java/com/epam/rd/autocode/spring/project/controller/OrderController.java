package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import com.epam.rd.autocode.spring.project.service.ClientService;
import com.epam.rd.autocode.spring.project.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final ClientService clientService;
    private final BookService bookService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public String myOrders(Model model, Principal principal) {
        var orders = orderService.getOrdersByClient(principal.getName());
        model.addAttribute("orders", orders);
        return "orders/list"; // <-- було "orders"
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/manage")
    public String manage(Model model, Principal principal) {
        var incoming = orderService.getIncomingOrders();
        var mine = orderService.getOrdersByEmployee(principal.getName());
        model.addAttribute("incoming", incoming);
        model.addAttribute("mine", mine);
        return "orders/manage";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Long id, Principal principal) {
        orderService.confirmOrder(id, principal.getName());
        return "redirect:/order/manage?confirmed";
    }

    @GetMapping("/cart")
    @PreAuthorize("hasRole('CLIENT')")
    public String cart(Model model, Principal principal) {
        var orders = orderService.getOrdersByClient(principal.getName());
        if (orders.isEmpty()) {
            model.addAttribute("order", new OrderDTO());
            model.addAttribute("books", java.util.Collections.emptyMap());
            return "cart";
        }

        var order = orders.get(0);
        var ids = order.getBookItems() == null
            ? java.util.Set.<Long>of()
            : order.getBookItems().stream().map(bi -> bi.getBookId()).collect(java.util.stream.Collectors.toSet());

        var booksMap = bookService.getAllBooks().stream()
            .filter(b -> ids.contains(b.getId()))
            .collect(java.util.stream.Collectors.toMap(
                BookDTO::getId,
                java.util.function.Function.identity()));


        model.addAttribute("order", order);
        model.addAttribute("books", booksMap);
        return "cart";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/update")
    public String updateQty(@RequestParam Long bookId,
                            @RequestParam Integer quantity,
                            Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        var orderOpt = orderService.getOrdersByClient(principal.getName()).stream().findFirst();
        if (orderOpt.isEmpty()) return "redirect:/order/cart";

        var order = orderOpt.get();
        if (order.getBookItems() == null) return "redirect:/order/cart";

        if (quantity == null || quantity < 1) {
            order.getBookItems().removeIf(i -> i.getBookId().equals(bookId));
        } else {
            for (var i : order.getBookItems()) {
                if (i.getBookId().equals(bookId)) {
                    i.setQuantity(quantity);
                    break;
                }
            }
        }
        order.setClientId(client.getId());
        orderService.saveDraft(order);
        return "redirect:/order/cart?updated";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam Long bookId, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        var orderOpt = orderService.getOrdersByClient(principal.getName()).stream().findFirst();
        if (orderOpt.isEmpty()) return "redirect:/order/cart";

        var order = orderOpt.get();
        if (order.getBookItems() != null) {
            order.getBookItems().removeIf(i -> i.getBookId().equals(bookId));
            order.setClientId(client.getId());
            orderService.saveDraft(order);
        }
        return "redirect:/order/cart?removed";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/clear")
    public String clearCart(Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        var orderOpt = orderService.getOrdersByClient(principal.getName()).stream().findFirst();
        if (orderOpt.isEmpty()) return "redirect:/order/cart";

        var order = orderOpt.get();
        order.setClientId(client.getId());
        order.setBookItems(new java.util.ArrayList<>());
        orderService.saveDraft(order);
        return "redirect:/order/cart?cleared";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/create")
    public String addToCart(@RequestParam Long bookId,
                            @RequestParam(required = false, defaultValue = "1") Integer quantity,
                            Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        var existing = orderService.getOrdersByClient(principal.getName()).stream().findFirst();

        var order = existing.orElseGet(() ->
            com.epam.rd.autocode.spring.project.dto.OrderDTO.builder()
                .clientId(client.getId())
                .bookItems(new java.util.ArrayList<>())
                .build()
        );
        order.setClientId(client.getId());
        if (order.getBookItems() == null) order.setBookItems(new java.util.ArrayList<>());

        var items = order.getBookItems();
        var q = quantity == null ? 1 : Math.max(1, quantity);
        var item = items.stream().filter(i -> i.getBookId().equals(bookId)).findFirst().orElse(null);
        if (item != null) {
            item.setQuantity((item.getQuantity() == null ? 0 : item.getQuantity()) + q);
        } else {
            items.add(com.epam.rd.autocode.spring.project.dto.BookItemDTO.builder()
                .bookId(bookId)
                .quantity(q)
                .build());
        }

        orderService.saveDraft(order);
        return "redirect:/order/cart?added";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/create")
    public String createOrUpdateForClient(@ModelAttribute OrderDTO order, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        order.setClientId(client.getId());
        orderService.saveDraft(order);
        return "redirect:/order/cart?saved";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/add")
    public String addForEmployee(@ModelAttribute OrderDTO order) {
        orderService.addOrder(order);
        return "redirect:/order/manage?created";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/save")
    public String saveDraft(@ModelAttribute OrderDTO order, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        order.setClientId(client.getId());
        orderService.saveDraft(order);
        return "redirect:/order/cart?saved";
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/cart/submit")
    public String submit(@ModelAttribute OrderDTO order, Principal principal) {
        var client = clientService.getClientByEmail(principal.getName());
        order.setClientId(client.getId());
        orderService.submit(order);
        return "redirect:/order?submitted";
    }
}
