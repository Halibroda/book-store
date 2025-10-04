package com.epam.rd.autocode.spring.project.controller;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    @GetMapping("/{email}")
    public ClientDTO getClient(@PathVariable String email) {
        return clientService.getClientByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDTO addClient(@Valid @RequestBody ClientDTO dto) {
        return clientService.addClient(dto);
    }

    @PutMapping("/{email}")
    public ClientDTO updateClient(@PathVariable String email, @Valid @RequestBody ClientDTO dto) {
        return clientService.updateClientByEmail(email, dto);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable String email) {
        clientService.deleteClientByEmail(email);
    }
}
