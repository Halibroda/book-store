package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.ClientDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.service.ClientService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ModelMapper mapper;

    public ClientServiceImpl(ClientRepository clientRepository, ModelMapper mapper) {
        this.clientRepository = clientRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
            .map(c -> mapper.map(c, ClientDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public ClientDTO getClientByEmail(String email) {
        Client client = clientRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        return mapper.map(client, ClientDTO.class);
    }

    @Override
    public ClientDTO updateClientByEmail(String email, ClientDTO dto) {
        Client existing = clientRepository.findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Client not found: " + email));
        Long id = existing.getId();
        mapper.map(dto, existing);
        existing.setId(id);
        Client saved = clientRepository.save(existing);
        return mapper.map(saved, ClientDTO.class);
    }

    @Override
    public void deleteClientByEmail(String email) {
        if (!clientRepository.existsByEmail(email)) {
            throw new NotFoundException("Client not found: " + email);
        }
        clientRepository.deleteByEmail(email);
    }

    @Override
    public ClientDTO addClient(ClientDTO dto) {
        if (clientRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistException("Client already exists: " + dto.getEmail());
        }
        Client entity = mapper.map(dto, Client.class);
        Client saved = clientRepository.save(entity);
        return mapper.map(saved, ClientDTO.class);
    }
}
