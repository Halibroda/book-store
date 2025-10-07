package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/page")
    public Page<BookDTO> pageBooks(@RequestParam(required = false) String genre,
                                   @RequestParam(required = false) Language language,
                                   @RequestParam(required = false) AgeGroup ageGroup,
                                   @RequestParam(required = false) BigDecimal minPrice,
                                   @RequestParam(required = false) BigDecimal maxPrice,
                                   @RequestParam(required = false, name = "q") String q,
                                   Pageable pageable) {
        return bookService.findBooks(genre, language, ageGroup, minPrice, maxPrice, q, pageable);
    }

    @GetMapping("/{name}")
    public BookDTO getBook(@PathVariable String name) {
        return bookService.getBookByName(name);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO addBook(@Valid @RequestBody BookDTO dto) {
        return bookService.addBook(dto);
    }

    @PutMapping("/{name}")
    public BookDTO updateBook(@PathVariable String name, @Valid @RequestBody BookDTO dto) {
        return bookService.updateBookByName(name, dto);
    }

    @DeleteMapping("/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable String name) {
        bookService.deleteBookByName(name);
    }
}
