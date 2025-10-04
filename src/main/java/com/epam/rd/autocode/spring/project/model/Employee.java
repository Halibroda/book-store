package com.epam.rd.autocode.spring.project.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"orders"})
@Entity
@Table(name = "employees")
public class Employee extends User {

    private String phone;

    private LocalDate birthDate;
}
