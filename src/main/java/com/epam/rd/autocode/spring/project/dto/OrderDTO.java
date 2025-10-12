package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;

    @NotNull
    private Long clientId;

    private Long employeeId;

    @PastOrPresent
    private LocalDateTime orderDate;

    @PositiveOrZero
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;

    @NotEmpty
    private List<BookItemDTO> bookItems;

    @NotEmpty
    private OrderStatus status;
}
