package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookDTO;
import com.epam.rd.autocode.spring.project.exception.AlreadyExistException;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper mapper) {
        this.bookRepository = bookRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
            .map(b -> mapper.map(b, BookDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public BookDTO getBookByName(String name) {
        Book book = bookRepository.findByName(name)
            .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        return mapper.map(book, BookDTO.class);
    }

    @Override
    public BookDTO updateBookByName(String name, BookDTO dto) {
        Book existing = bookRepository.findByName(name)
            .orElseThrow(() -> new NotFoundException("Book not found: " + name));
        Long id = existing.getId();
        mapper.map(dto, existing);
        existing.setId(id);
        Book saved = bookRepository.save(existing);
        return mapper.map(saved, BookDTO.class);
    }

    @Override
    public void deleteBookByName(String name) {
        if (!bookRepository.existsByName(name)) {
            throw new NotFoundException("Book not found: " + name);
        }
        bookRepository.deleteByName(name);
    }

    @Override
    public BookDTO addBook(BookDTO dto) {
        if (bookRepository.existsByName(dto.getName())) {
            throw new AlreadyExistException("Book already exists: " + dto.getName());
        }
        Book entity = mapper.map(dto, Book.class);
        Book saved = bookRepository.save(entity);
        return mapper.map(saved, BookDTO.class);
    }
}
