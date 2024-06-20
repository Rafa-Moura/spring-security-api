package br.com.rafaelmoura.spring_security_api.service.mapper;

import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.entity.Product;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class ProductServiceImplMapper {
    public ProductResponseDTO entityProductToProductResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .product(product.getProduct())
                .serialNumber(product.getSerialNumber())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }

    public Product productRequestDTOToProductEntity(ProductRequestDTO productRequestDTO) {

        return Product.builder()
                .product(productRequestDTO.getProduct())
                .serialNumber(productRequestDTO.getSerialNumber())
                .price(productRequestDTO.getPrice())
                .quantity(productRequestDTO.getQuantity())
                .build();

    }

    public PageableResponseDTO pageEntityToPageableResponseDTO(Page<Product> page) {
        return PageableResponseDTO.builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalRecords(page.getTotalElements())
                .firstPage(page.isFirst())
                .lastPage(page.isLast())
                .build();
    }
}
