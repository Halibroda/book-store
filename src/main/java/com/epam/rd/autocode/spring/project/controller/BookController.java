package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
