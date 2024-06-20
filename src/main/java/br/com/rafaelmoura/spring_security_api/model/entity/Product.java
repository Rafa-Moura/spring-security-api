package br.com.rafaelmoura.spring_security_api.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tb_produtos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ToString.Include
    private String product;
    @EqualsAndHashCode.Include
    @ToString.Include
    private String serialNumber;
    @ToString.Include
    private BigDecimal price;
    @ToString.Include
    private int quantity;
}
