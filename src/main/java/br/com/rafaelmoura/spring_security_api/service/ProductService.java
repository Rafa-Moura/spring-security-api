package br.com.rafaelmoura.spring_security_api.service;

import br.com.rafaelmoura.spring_security_api.exceptions.ProductNotFoundException;
import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponseDTO insertProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO findProductBySerialNumber(String serialNumber) throws ProductNotFoundException;

    PageableResponseDTO findAllProducts(Pageable pageable);

    ProductResponseDTO updateProductBySerialNumber(String serialNumber, ProductRequestDTO productRequestDTO) throws ProductNotFoundException;

    void deleteProductBySerialNumber(String serialNumber) throws ProductNotFoundException;

}
