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

    @Override
    public OrderDTO saveDraft(OrderDTO dto) {
        if (dto.getClientId() == null) {
            throw new NotFoundException("Client id is required");
        }
        var client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new NotFoundException("Client not found: " + dto.getClientId()));

        var existing = orderRepository.findAllByClient_Email(client.getEmail());
        Order order = existing.isEmpty() ? new Order() : existing.get(0);
        order.setClient(client);
        if (order.getOrderDate() == null) order.setOrderDate(LocalDateTime.now());

        if (order.getBookItems() == null) {
            order.setBookItems(new java.util.ArrayList<>());
        } else {
            order.getBookItems().clear();
        }

        if (dto.getBookItems() != null && !dto.getBookItems().isEmpty()) {
            for (var bi : dto.getBookItems()) {
                var book = bookRepository.findById(bi.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + bi.getBookId()));
                var item = new BookItem();
                item.setOrder(order);
                item.setBook(book);
                item.setQuantity(bi.getQuantity() == null ? 1 : Math.max(1, bi.getQuantity()));
                order.getBookItems().add(item);
            }
        }

        recalcTotal(order);
        var saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }

    @Override
    public OrderDTO submit(OrderDTO dto) {
        if (dto.getClientId() == null) {
            throw new NotFoundException("Client id is required");
        }
        var client = clientRepository.findById(dto.getClientId())
            .orElseThrow(() -> new NotFoundException("Client not found: " + dto.getClientId()));

        var existing = orderRepository.findAllByClient_Email(client.getEmail());
        if (existing.isEmpty()) {
            throw new NotFoundException("Order not found for client: " + client.getEmail());
        }
        Order order = existing.get(0);

        if (dto.getBookItems() != null) {
            if (order.getBookItems() == null) {
                order.setBookItems(new java.util.ArrayList<>());
            } else {
                order.getBookItems().clear();
            }
            for (var bi : dto.getBookItems()) {
                var book = bookRepository.findById(bi.getBookId())
                    .orElseThrow(() -> new NotFoundException("Book not found: " + bi.getBookId()));
                var item = new BookItem();
                item.setOrder(order);
                item.setBook(book);
                item.setQuantity(bi.getQuantity() == null ? 1 : Math.max(1, bi.getQuantity()));
                order.getBookItems().add(item);
            }
        }

        recalcTotal(order);
        order.setOrderDate(LocalDateTime.now());
        var saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }

    private void recalcTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        if (order.getBookItems() != null) {
            for (BookItem it : order.getBookItems()) {
                BigDecimal price = it.getBook() != null ? it.getBook().getPrice() : BigDecimal.ZERO;
                int qty = it.getQuantity() == null ? 0 : it.getQuantity();
                if (price != null && qty > 0) {
                    total = total.add(price.multiply(BigDecimal.valueOf(qty)));
                }
            }
        }
        order.setPrice(total);
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

    @Override
    public OrderDTO confirmOrder(Long orderId, String employeeEmail) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found: " + orderId));
        Employee employee = employeeRepository.findByEmail(employeeEmail)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeEmail));
        order.setEmployee(employee);
        Order saved = orderRepository.save(order);
        return mapper.map(saved, OrderDTO.class);
    }
}
