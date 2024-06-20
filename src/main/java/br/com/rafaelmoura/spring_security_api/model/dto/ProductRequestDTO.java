package br.com.rafaelmoura.spring_security_api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    private String product;
    private String serialNumber;
    private BigDecimal price;
    private int quantity;
}
