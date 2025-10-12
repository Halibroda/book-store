package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockBean BookService bookService;

    @Test
    void getAllBooks_permitAll() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of(
            BookDTO.builder().id(1L).name("A").build(),
            BookDTO.builder().id(2L).name("B").build()
        ));
        mvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].name").value("A"));
    }

    @Test
    void addBook_unauthorized() throws Exception {
        BookDTO dto = BookDTO.builder().name("X").price(new BigDecimal("10")).build();
        mvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
            .andExpect(status().isUnauthorized());
    }

    @WithMockUser(roles = "EMPLOYEE")
    @Test
    void addBook_asEmployee_ok() throws Exception {
        BookDTO in = BookDTO.builder().name("X").price(new BigDecimal("10")).build();
        BookDTO out = BookDTO.builder().id(10L).name("X").price(new BigDecimal("10")).build();
        when(bookService.addBook(in)).thenReturn(out);

        mvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(in)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10));
    }

    @WithMockUser(roles = "CLIENT")
    @Test
    void deleteBook_asClient_forbidden() throws Exception {
        mvc.perform(delete("/api/books/X"))
            .andExpect(status().isForbidden());
    }
}
