package br.com.rafaelmoura.spring_security_api.service.impl;

import br.com.rafaelmoura.spring_security_api.exceptions.ProductNotFoundException;
import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.entity.Product;
import br.com.rafaelmoura.spring_security_api.repository.ProductRepository;
import br.com.rafaelmoura.spring_security_api.service.ProductService;
import br.com.rafaelmoura.spring_security_api.service.mapper.ProductServiceImplMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String PRODUCT_NOT_FOUND_LOG_ERROR = "Produto nao localizado com o serialNumber [{}]";
    public static final String PRODUCT_NOT_FOUND_ERROR_MESSAGE = "Produto nao localizado com o serialNumber informado";
    private final ProductRepository productRepository;

    @Override
    public ProductResponseDTO insertProduct(ProductRequestDTO productRequestDTO) {

        Optional<Product> productOptional = productRepository.findBySerialNumber(productRequestDTO.getSerialNumber());

        if (productOptional.isPresent()) {
            log.info("Produto [{}] com serialNumber [{}] encontrado na base de dados com estoque atual de [{}]",
                    productRequestDTO.getProduct(), productRequestDTO.getSerialNumber(),
                    productOptional.get().getQuantity());

            Product product = productOptional.get();
            int updateStock = product.getQuantity() + productRequestDTO.getQuantity();
            product.setQuantity(updateStock);

            log.info("Produto [{}] com serialNumber [{}] atualizado. Estoque atual [{}]",
                    productRequestDTO.getProduct(), productRequestDTO.getSerialNumber(),
                    productOptional.get().getQuantity());
            return saveAndMapProduct(product);
        }

        Product product = ProductServiceImplMapper.productRequestDTOToProductEntity(productRequestDTO);

        log.info("[{}] unidades do produto [{}] com serialNumber [{}] foram inseridas com sucesso na base",
                productRequestDTO.getQuantity(), product.getProduct(), product.getSerialNumber());

        return saveAndMapProduct(product);
    }

    @Override
    public ProductResponseDTO findProductBySerialNumber(String serialNumber) throws ProductNotFoundException {

        log.info("Iniciando busca do produto com serialNumber [{}]", serialNumber);

        Product product = getProductBySerialNumber(serialNumber);

        log.info("Produto [{}] com serialNumber [{}] localizado com sucesso", product.getProduct(),
                product.getSerialNumber());

        return ProductServiceImplMapper.entityProductToProductResponseDTO(product);
    }

    @Override
    public PageableResponseDTO findAllProducts(Pageable pageable) {
        log.info("Iniciando busca de [{}] produtos na pagina [{}]", pageable.getPageSize(), pageable.getPageNumber());

        Page<Product> products = productRepository.findAll(pageable);

        log.info("Finalizando busca de [{}] produtos na pagina [{}]", pageable.getPageSize(), pageable.getPageNumber());
        return ProductServiceImplMapper.pageEntityToPageableResponseDTO(products);
    }

    @Override
    public ProductResponseDTO updateProductBySerialNumber(String serialNumber, ProductRequestDTO productRequestDTO) throws ProductNotFoundException {

        log.info("Iniciando busca do produto com serialNumber [{}] para atualizacao", serialNumber);

        Product product = getProductBySerialNumber(serialNumber);

        product.setProduct(productRequestDTO.getProduct());
        product.setSerialNumber(productRequestDTO.getSerialNumber());
        product.setPrice(productRequestDTO.getPrice());
        product.setQuantity(product.getQuantity());

        log.info("Produto [{}] com serialNumber [{}] atualizado com sucesso", product.getProduct(),
                product.getSerialNumber());

        return saveAndMapProduct(product);
    }

    @Override
    public void deleteProductBySerialNumber(String serialNumber) throws ProductNotFoundException {
        log.info("Iniciando busca do produto com serialNumber [{}] para remocao", serialNumber);

        Product product = getProductBySerialNumber(serialNumber);

        productRepository.delete(product);

        log.info("Produto [{}] com serialNumber [{}] deletado com sucesso", product.getProduct(),
                product.getSerialNumber());
    }

    private ProductResponseDTO saveAndMapProduct(Product product) {
        Product savedProduct = productRepository.save(product);
        return ProductServiceImplMapper.entityProductToProductResponseDTO(savedProduct);
    }

    private Product getProductBySerialNumber(String serialNumber) throws ProductNotFoundException {
        return productRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> {
                    log.error(PRODUCT_NOT_FOUND_LOG_ERROR, serialNumber);
                    return new ProductNotFoundException(PRODUCT_NOT_FOUND_ERROR_MESSAGE);
                });
    }
}
