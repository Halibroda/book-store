package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.*;
import com.epam.rd.autocode.spring.project.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock ClientRepository clientRepository;
    @Mock EmployeeRepository employeeRepository;
    @Mock BookRepository bookRepository;
    OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new OrderServiceImpl(orderRepository, clientRepository, employeeRepository,bookRepository,new ModelMapper());
    }

    @Test
    void addOrder_computesTotalAndSaves() {
        OrderDTO in = OrderDTO.builder()
            .clientId(1L)
            .orderDate(LocalDateTime.now())
            .bookItems(List.of(
                BookItemDTO.builder().bookId(10L).quantity(2).build(),
                BookItemDTO.builder().bookId(11L).quantity(1).build()
            ))
            .build();

        Client client = new Client();
        client.setId(1L);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        Book b1 = new Book();
        b1.setId(10L);
        b1.setPrice(new BigDecimal("24.99"));
        Book b2 = new Book();
        b2.setId(11L);
        b2.setPrice(new BigDecimal("16.50"));
        when(bookRepository.findById(10L)).thenReturn(Optional.of(b1));
        when(bookRepository.findById(11L)).thenReturn(Optional.of(b2));

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(100L);
            return o;
        });

        OrderDTO out = service.addOrder(in);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertEquals(new BigDecimal("66.48"), saved.getPrice());
        assertEquals(100L, out.getId());
    }

    @Test
    void addOrder_clientNotFound() {
        OrderDTO in = OrderDTO.builder().clientId(99L).build();
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.addOrder(in));
    }

    @Test
    void getOrdersByClient_maps() {
        when(orderRepository.findAllByClient_Email("a@b")).thenReturn(List.of(new Order(), new Order()));
        assertEquals(2, service.getOrdersByClient("a@b").size());
    }
}
