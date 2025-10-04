package com.epam.rd.autocode.spring.project.service.impl;

import com.epam.rd.autocode.spring.project.dto.BookItemDTO;
import com.epam.rd.autocode.spring.project.dto.OrderDTO;
import com.epam.rd.autocode.spring.project.exception.NotFoundException;
import com.epam.rd.autocode.spring.project.model.Book;
import com.epam.rd.autocode.spring.project.model.BookItem;
import com.epam.rd.autocode.spring.project.model.Client;
import com.epam.rd.autocode.spring.project.model.Employee;
import com.epam.rd.autocode.spring.project.model.Order;
import com.epam.rd.autocode.spring.project.repo.BookRepository;
import com.epam.rd.autocode.spring.project.repo.ClientRepository;
import com.epam.rd.autocode.spring.project.repo.EmployeeRepository;
import com.epam.rd.autocode.spring.project.repo.OrderRepository;
import com.epam.rd.autocode.spring.project.service.OrderService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final BookRepository bookRepository;
    private final ModelMapper mapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientRepository clientRepository,
                            EmployeeRepository employeeRepository,
                            BookRepository bookRepository,
                            ModelMapper mapper) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.bookRepository = bookRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getOrdersByClient(String clientEmail) {
        return orderRepository.findAllByClient_Email(clientEmail).stream()
            .map(o -> mapper.map(o, OrderDTO.class))
            .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<OrderDTO> getOrdersByEmployee(String employeeEmail) {
        return orderRepository.findAllByEmployee_Email(employeeEmail).stream()
            .map(o -> mapper.map(o, OrderDTO.class))
            .toList();
    }

    @Override
    public OrderDTO addOrder(OrderDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new NotFoundException("Client not found: " + dto.getClientId()));

        Employee employee = null;
        if (dto.getEmployeeId() != null) {
            employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new NotFoundException("Employee not found: " + dto.getEmployeeId()));
        }

        Order order = new Order();
        order.setClient(client);
        order.setEmployee(employee);
        order.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        if (dto.getBookItems() != null && !dto.getBookItems().isEmpty()) {
            for (BookItemDTO itemDTO : dto.getBookItems()) {
                Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + itemDTO.getBookId()));
                BookItem item = new BookItem();
                item.setOrder(order);
                item.setBook(book);
                item.setQuantity(itemDTO.getQuantity());
                order.getBookItems().add(item);
                if (book.getPrice() != null && itemDTO.getQuantity() != null) {
                    total = total.add(book.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
                }
            }
        }
        order.setPrice(dto.getPrice() != null ? dto.getPrice() : total);

        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }
}
