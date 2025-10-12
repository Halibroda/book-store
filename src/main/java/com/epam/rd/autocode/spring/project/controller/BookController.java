package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
    public String list(@RequestParam(required = false) String genre,
                       @RequestParam(required = false) Language language,
                       @RequestParam(required = false) AgeGroup ageGroup,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(required = false, name = "search") String search,
                       @PageableDefault(size = 12, sort = "name") Pageable pageable,
                       Model model) {
        var page = bookService.findBooks(genre, language, ageGroup, minPrice, maxPrice, search, pageable);
        model.addAttribute("page", page);
        model.addAttribute("params", Map.of(
            "genre", genre, "language", language, "ageGroup", ageGroup,
            "minPrice", minPrice, "maxPrice", maxPrice, "search", search));
        return "books/list";
    }

    @GetMapping("/{name}")
    public String details(@PathVariable String name, Model model) {
        model.addAttribute("book", bookService.getBookByName(name));
        return "books/details";
    }
}

