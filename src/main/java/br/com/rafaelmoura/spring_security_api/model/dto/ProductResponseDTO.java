package br.com.rafaelmoura.spring_security_api.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private String product;
    private String serialNumber;
    private BigDecimal price;
    private int quantity;
}
