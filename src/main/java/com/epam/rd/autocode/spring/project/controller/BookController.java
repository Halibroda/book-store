package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final BookService bookService;

    @GetMapping("/{name}")
    public String details(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "book-details";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/manage")
    public String manage(Model model) {
        log.info("books.manage.view");
        model.addAttribute("books", bookService.getAllBooks());
        return "books/manage";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("book", new BookDTO());
        return "books/book-form";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public String create(@Valid @ModelAttribute("book") BookDTO book, BindingResult br) {
        if (br.hasErrors()) return "books/book-form";
        bookService.addBook(book);
        log.info("books.create name={}", book.getName());
        return "redirect:/book/manage?created";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/{name}/edit")
    public String editForm(@PathVariable String name, Model model) {
        var dto = bookService.getBookByName(name);
        model.addAttribute("book", dto);
        model.addAttribute("originalName", name);
        return "books/book-form";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{name}/edit")
    public String update(@PathVariable("name") String originalName,
                         @Valid @ModelAttribute("book") BookDTO book,
                         BindingResult br,
                         Model model) {
        if (br.hasErrors()) {
            model.addAttribute("originalName", originalName);
            return "books/book-form";
        }
        bookService.updateBookByName(originalName, book);
        log.info("books.update nameOld={} nameNew={}", originalName, book.getName());
        return "redirect:/book/manage?updated";
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{name}/delete")
    public String delete(@PathVariable String name) {
        bookService.deleteBookByName(name);
        log.warn("books.delete name={}", name);
        return "redirect:/book/manage?deleted";
    }
}
