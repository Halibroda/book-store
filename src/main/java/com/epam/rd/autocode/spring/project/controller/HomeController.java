package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping("/")
    public String home(@RequestParam(required = false) String genre,
                       @RequestParam(required = false) Language language,
                       @RequestParam(required = false) AgeGroup ageGroup,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       @RequestParam(required = false, name = "search") String search,
                       @PageableDefault(size = 12, sort = "name") Pageable pageable,
                       Model model) {

        Page<BookDTO> page = bookService.findBooks(genre, language, ageGroup, minPrice, maxPrice, search, pageable);

        Map<String, Object> params = new HashMap<>();
        params.put("genre", genre);
        params.put("language", language);
        params.put("ageGroup", ageGroup);
        params.put("minPrice", minPrice);
        params.put("maxPrice", maxPrice);
        params.put("search", search);

        model.addAttribute("page", page);
        model.addAttribute("params", params);
        return "index";
    }
}
