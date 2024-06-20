package br.com.rafaelmoura.spring_security_api.controller;

import br.com.rafaelmoura.spring_security_api.exceptions.ProductNotFoundException;
import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import br.com.rafaelmoura.spring_security_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/products")
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/v1")
    public ResponseEntity<ProductResponseDTO> insertProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        log.info("Iniciando fluxo para inserir [{}] unidades do produto [{}] com serialNumber [{}]",
                productRequestDTO.getQuantity(), productRequestDTO.getProduct(), productRequestDTO.getSerialNumber());

        ProductResponseDTO productResponseDTO = productService.insertProduct(productRequestDTO);

        log.info("Finalizando fluxo para inserir o produto [{}] com serialNumber [{}]",
                productRequestDTO.getProduct(), productRequestDTO.getSerialNumber());

        return new ResponseEntity<>(productResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping(value = "/v1/{serialNumber}")
    public ResponseEntity<ProductResponseDTO> findProductBySerialNumber(@PathVariable String serialNumber) throws ProductNotFoundException {
        log.info("Iniciando fluxo para buscar o produto com serialNumber [{}]",
                serialNumber);

        ProductResponseDTO productResponseDTO = productService.findProductBySerialNumber(serialNumber);

        log.info("Finalizando fluxo para buscar o produto com serialNumber [{}]",
                serialNumber);

        return new ResponseEntity<>(productResponseDTO, HttpStatus.OK);
    }

    @GetMapping(value = "/v1")
    public ResponseEntity<PageableResponseDTO> findAllProducts(Pageable pageable) {
        log.info("Iniciando fluxo para recuperar [{}] produtos da pagina [{}]", pageable.getPageSize(),
                pageable.getPageNumber());

        PageableResponseDTO pageableResponseDTO = productService.findAllProducts(pageable);

        log.info("Finalizando fluxo para recuperar produtos. Total de paginas [{}], total de elementos [{}]",
                pageableResponseDTO.getTotalPages(), pageableResponseDTO.getTotalRecords());

        return new ResponseEntity<>(pageableResponseDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/v1/{serialNumber}")
    public ResponseEntity<ProductResponseDTO> updateProductBySerialNumber(@PathVariable String serialNumber,
                                                                          @RequestBody ProductRequestDTO productRequestDTO) throws ProductNotFoundException {
        log.info("Iniciando fluxo para buscar o produto com serialNumber [{}]",
                serialNumber);

        ProductResponseDTO productResponseDTO = productService.updateProductBySerialNumber(serialNumber, productRequestDTO);

        log.info("Finalizando fluxo para buscar o produto com serialNumber [{}]",
                serialNumber);

        return new ResponseEntity<>(productResponseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/v1/{serialNumber}")
    public ResponseEntity<ProductResponseDTO> deleteProductBySerialNumber(@PathVariable String serialNumber) throws ProductNotFoundException {
        log.info("Iniciando fluxo para deletar o produto com serialNumber [{}]",
                serialNumber);

        productService.deleteProductBySerialNumber(serialNumber);

        log.info("Finalizando fluxo para deletar o produto com serialNumber [{}]",
                serialNumber);

        return ResponseEntity.noContent().build();
    }
}
