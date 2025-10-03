package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDTO {
    private Long id;

    @NotNull
    private Long clientId;

    private Long employeeId;

    private LocalDateTime orderDate;

    @PositiveOrZero
    private BigDecimal price;

    private List<BookItemDTO> bookItems;
}
