package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
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
class ClientServiceImplTest {

    @Mock ClientRepository clientRepository;
    ClientServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClientServiceImpl(clientRepository, new ModelMapper());
    }

    @Test
    void getAllClients() {
        when(clientRepository.findAll()).thenReturn(List.of(new Client(), new Client()));
        assertEquals(2, service.getAllClients().size());
    }

    @Test
    void getByEmail_notFound() {
        when(clientRepository.findByEmail("x@x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getClientByEmail("x@x"));
    }

    @Test
    void addClient_success() {
        ClientDTO in = ClientDTO.builder().email("x@x").name("X").balance(new BigDecimal("1")).build();
        when(clientRepository.existsByEmail("x@x")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> {
            Client c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });
        ClientDTO out = service.addClient(in);
        assertEquals(10L, out.getId());
    }

    @Test
    void addClient_exists() {
        ClientDTO in = ClientDTO.builder().email("x@x").build();
        when(clientRepository.existsByEmail("x@x")).thenReturn(true);
        assertThrows(AlreadyExistException.class, () -> service.addClient(in));
    }
}
