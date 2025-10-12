package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/{name}")
    public String details(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "book-details";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "book-form";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("book") BookDTO book, BindingResult br) {
        if (br.hasErrors()) return "book-form";
        bookService.addBook(book);
        return "redirect:/?created";
    }

}
