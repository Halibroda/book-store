package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookItemDTO {
    private Long id;

    @NotNull
    private Long bookId;

    @Min(1)
    private Integer quantity;
}
