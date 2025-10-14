package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookControllerTest {

    BookService bookService;
    BookController controller;

    @BeforeEach
    void setUp() {
        bookService = mock(BookService.class);
        controller = new BookController(bookService);
    }

    @Test
    void details_returnsViewAndModel() {
        Model model = new ExtendedModelMap();
        BookDTO dto = BookDTO.builder().name("N").author("A").price(new BigDecimal("10.00")).build();
        when(bookService.getBookByName("N")).thenReturn(dto);

        String view = controller.details("N", model);

        assertThat(view).isEqualTo("book-details");
        assertThat(model.getAttribute("book")).isEqualTo(dto);
        verify(bookService).getBookByName("N");
    }

    @Test
    void manage_returnsBooksListInModel() {
        Model model = new ExtendedModelMap();
        when(bookService.getAllBooks()).thenReturn(List.of(new BookDTO()));

        String view = controller.manage(model);

        assertThat(view).isEqualTo("books/manage");
        assertThat(model.getAttribute("books")).isInstanceOf(List.class);
        verify(bookService).getAllBooks();
    }

    @Test
    void createForm_returnsFormView_withEmptyDto() {
        Model model = new ExtendedModelMap();

        String view = controller.createForm(model);

        assertThat(view).isEqualTo("books/book-form");
        assertThat(model.getAttribute("book")).isInstanceOf(BookDTO.class);
    }

    @Test
    void create_whenValidationErrors_returnsForm() {
        BookDTO dto = new BookDTO();
        BindingResult br = new BeanPropertyBindingResult(dto, "book");
        br.rejectValue("name", "NotBlank", "required");

        String view = controller.create(dto, br);

        assertThat(view).isEqualTo("books/book-form");
        verifyNoInteractions(bookService);
    }

    @Test
    void create_whenValid_callsService_andRedirects() {
        BookDTO dto = BookDTO.builder().name("X").author("A").price(new BigDecimal("1.00")).build();
        BindingResult br = new BeanPropertyBindingResult(dto, "book");

        String view = controller.create(dto, br);

        assertThat(view).isEqualTo("redirect:/book/manage?created");
        verify(bookService).addBook(dto);
    }

    @Test
    void editForm_loadsDto_andReturnsFormView() {
        Model model = new ExtendedModelMap();
        BookDTO dto = BookDTO.builder().name("Old").build();
        when(bookService.getBookByName("Old")).thenReturn(dto);

        String view = controller.editForm("Old", model);

        assertThat(view).isEqualTo("books/book-form");
        assertThat(model.getAttribute("book")).isEqualTo(dto);
        assertThat(model.getAttribute("originalName")).isEqualTo("Old");
    }

    @Test
    void update_withErrors_returnsFormAndKeepsOriginalName() {
        BookDTO dto = BookDTO.builder().name("").build();
        BindingResult br = new BeanPropertyBindingResult(dto, "book");
        br.rejectValue("name", "NotBlank", "required");
        Model model = new ExtendedModelMap();

        String view = controller.update("Old", dto, br, model);

        assertThat(view).isEqualTo("books/book-form");
        assertThat(model.getAttribute("originalName")).isEqualTo("Old");
        verifyNoInteractions(bookService);
    }

    @Test
    void update_valid_callsService_andRedirects() {
        BookDTO dto = BookDTO.builder().name("New").build();
        BindingResult br = new BeanPropertyBindingResult(dto, "book");
        Model model = new ExtendedModelMap();

        String view = controller.update("Old", dto, br, model);

        assertThat(view).isEqualTo("redirect:/book/manage?updated");
        verify(bookService).updateBookByName("Old", dto);
    }

    @Test
    void delete_callsService_andRedirects() {
        String view = controller.delete("Name");
        assertThat(view).isEqualTo("redirect:/book/manage?deleted");
        verify(bookService).deleteBookByName("Name");
    }
}
