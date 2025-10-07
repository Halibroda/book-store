package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock BookRepository bookRepository;
    BookServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new BookServiceImpl(bookRepository, new ModelMapper());
    }

    @Test
    void addBook_success() {
        BookDTO in = BookDTO.builder().name("X").price(new BigDecimal("10")).build();
        when(bookRepository.existsByName("X")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(inv -> {
            Book b = inv.getArgument(0);
            b.setId(1L);
            return b;
        });
        BookDTO out = service.addBook(in);
        assertEquals(1L, out.getId());
        assertEquals("X", out.getName());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBook_alreadyExists() {
        BookDTO in = BookDTO.builder().name("X").build();
        when(bookRepository.existsByName("X")).thenReturn(true);
        assertThrows(AlreadyExistException.class, () -> service.addBook(in));
        verify(bookRepository, never()).save(any());
    }

    @Test
    void getBookByName_notFound() {
        when(bookRepository.findByName("Z")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getBookByName("Z"));
    }

    @Test
    void updateBook_success() {
        Book existing = new Book();
        existing.setId(5L);
        existing.setName("Old");
        when(bookRepository.findByName("Old")).thenReturn(Optional.of(existing));
        when(bookRepository.save(existing)).thenReturn(existing);
        BookDTO dto = BookDTO.builder().name("New").build();
        BookDTO out = service.updateBookByName("Old", dto);
        assertEquals(5L, out.getId());
        assertEquals("New", out.getName());
    }

    @Test
    void deleteBook_notFound() {
        when(bookRepository.existsByName("Nope")).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.deleteBookByName("Nope"));
    }
}
