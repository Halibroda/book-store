package com.epam.rd.autocode.spring.project.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {
    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String name;

    @NotNull
    @Digits(integer = 10, fraction = 2)
    @PositiveOrZero
    private BigDecimal balance;

    @NotNull
    private Boolean enabled;
}
