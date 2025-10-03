package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"orders"})
@Entity
@Table(name = "clients")
public class Client extends User {

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
}
