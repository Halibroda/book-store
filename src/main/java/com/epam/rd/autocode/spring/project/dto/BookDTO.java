package com.epam.rd.autocode.spring.project.dto;

import com.epam.rd.autocode.spring.project.model.enums.AgeGroup;
import com.epam.rd.autocode.spring.project.model.enums.Language;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BookDTO {
    private Long id;

    @NotBlank
    private String name;

    private String genre;

    @NotNull
    private AgeGroup ageGroup;

    @NotNull @DecimalMin("0.0")
    private BigDecimal price;

    private LocalDate publicationDate;

    private String author;

    @Min(1)
    private Integer pages;

    private String characteristics;

    private String description;

    @NotNull
    private Language language;
}
