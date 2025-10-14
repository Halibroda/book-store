package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    BookService bookService;
    HomeController controller;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        controller = new HomeController(bookService);
    }

    @Test
    void home_returnsIndex_withPageAndParams() {
        Pageable pageable = PageRequest.of(0, 8, Sort.by("name"));
        Page<BookDTO> page = new PageImpl<>(List.of(new BookDTO()), pageable, 20);
        when(bookService.findBooks(
            isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageable)))
            .thenReturn(page);

        Model model = new ExtendedModelMap();
        String view = controller.home(null, null, null, null, null, null, pageable, model);

        assertThat(view).isEqualTo("index");
        assertThat(model.getAttribute("page")).isEqualTo(page);

        Object params = model.getAttribute("params");
        assertThat(params).isInstanceOf(Map.class);
        verify(bookService).findBooks(null, null, null, null, null, null, pageable);
    }
}
