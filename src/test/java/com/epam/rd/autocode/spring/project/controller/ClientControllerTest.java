package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.security.JwtAuthFilter;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    ClientService clientService;
    ClientController controller;

    Principal principal(String email) {
        return () -> email;
    }

    @BeforeEach
    void setUp() {
        clientService = mock(ClientService.class);
        controller = new ClientController(clientService);
    }

    @Test
    void me_returnsProfileView_andModel() {
        String email = "client1@example.com";
        ClientDTO dto = ClientDTO.builder()
            .id(1L).email(email).name("John").balance(new BigDecimal("10.00")).build();
        when(clientService.getClientByEmail(email)).thenReturn(dto);

        Model model = new ExtendedModelMap();
        String view = controller.me(model, principal(email));

        assertThat(view).isEqualTo("client-profile");
        assertThat(model.getAttribute("client")).isEqualTo(dto);
        assertThat(model.getAttribute("form")).isEqualTo(dto);
    }

    @Test
    void updateMe_withErrors_redirectsWithFlashError() {
        String email = "client1@example.com";
        ClientDTO form = ClientDTO.builder().email(email).name("").balance(BigDecimal.ZERO).build();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        br.rejectValue("name", "NotBlank", "required");
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.updateMe(form, br, principal(email), ra);

        assertThat(view).isEqualTo("redirect:/clients/me");
        assertThat(ra.getFlashAttributes()).containsKey("error");
        verifyNoInteractions(clientService);
    }

    @Test
    void updateMe_success_callsService_andRedirects() {
        String email = "client1@example.com";
        ClientDTO form = ClientDTO.builder().email(email).name("New Name").balance(new BigDecimal("15.50")).build();
        BindingResult br = new BeanPropertyBindingResult(form, "form");
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        when(clientService.updateClientByEmail(eq(email), any(ClientDTO.class)))
            .thenAnswer(inv -> inv.getArgument(1));

        String view = controller.updateMe(form, br, principal(email), ra);

        assertThat(view).isEqualTo("redirect:/clients/me");
        assertThat(ra.getFlashAttributes()).containsKey("success");

        ArgumentCaptor<ClientDTO> captor = ArgumentCaptor.forClass(ClientDTO.class);
        verify(clientService).updateClientByEmail(eq(email), captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(email);
        assertThat(captor.getValue().getName()).isEqualTo("New Name");
        assertThat(captor.getValue().getBalance()).isEqualByComparingTo("15.50");
    }

    @Test
    void deleteMe_deletes_andClearsCookie_andRedirects() {
        String email = "client1@example.com";
        MockHttpServletResponse resp = new MockHttpServletResponse();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        String view = controller.deleteMe(principal(email), resp, ra);

        assertThat(view).isEqualTo("redirect:/login?deleted");
        verify(clientService).deleteClientByEmail(email);
        String setCookie = resp.getHeader("Set-Cookie");
        assertThat(setCookie).contains(JwtAuthFilter.ACCESS_COOKIE);
        assertThat(setCookie).contains("Max-Age=0");
    }

    @Test
    void manage_listsClients_forEmployeePanel() {
        when(clientService.getAllClients()).thenReturn(List.of(new ClientDTO()));
        Model model = new ExtendedModelMap();

        String view = controller.manage(model);

        assertThat(view).isEqualTo("clients/manage");
        assertThat(model.getAttribute("clients")).isInstanceOf(List.class);
        verify(clientService).getAllClients();
    }

    @Test
    void block_callsService_andRedirects() {
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String view = controller.block("user@e.com", ra);

        assertThat(view).isEqualTo("redirect:/clients/manage");
        assertThat(ra.getFlashAttributes()).containsKey("success");
        verify(clientService).blockByEmail("user@e.com");
    }

    @Test
    void unblock_callsService_andRedirects() {
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();
        String view = controller.unblock("user@e.com", ra);

        assertThat(view).isEqualTo("redirect:/clients/manage");
        assertThat(ra.getFlashAttributes()).containsKey("success");
        verify(clientService).unblockByEmail("user@e.com");
    }
}
