package br.com.rafaelmoura.spring_security_api.service;

import br.com.rafaelmoura.spring_security_api.exceptions.ProductNotFoundException;
import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.entity.Product;
import br.com.rafaelmoura.spring_security_api.repository.ProductRepository;
import br.com.rafaelmoura.spring_security_api.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ProductServiceImplTest {

    public static final String SERIAL_NUMBER = "AAASD93847";
    @Mock
    ProductRepository productRepository;
    @InjectMocks
    ProductServiceImpl productService;

    PageableResponseDTO pageableResponseDto;
    ProductResponseDTO productResponseDto;
    ProductRequestDTO productRequestDto;
    Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productRequestDto = ProductRequestDTO.builder()
                .product("Cabo USB tipo C")
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3049.44))
                .quantity(23)
                .build();

        productResponseDto = ProductResponseDTO.builder()
                .quantity(23)
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3049.44))
                .product("Cabo USB tipo C")
                .build();

        pageableResponseDto = PageableResponseDTO.builder()
                .content(List.of(Product.builder()
                        .quantity(23)
                        .serialNumber(SERIAL_NUMBER)
                        .price(BigDecimal.valueOf(3049.44))
                        .product("Cabo USB tipo C")
                        .build()))
                .totalPages(1)
                .firstPage(true)
                .lastPage(true)
                .pageNumber(0)
                .totalRecords(1L)
                .build();

        product = Product.builder()
                .quantity(23)
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3049.44))
                .product("Cabo USB tipo C")
                .build();
    }

    @Test
    @DisplayName(value = "Devera inserir um produto no banco de dados")
    void mustBeInsertNewProduct() {

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO response = productService.insertProduct(productRequestDto);

        Assertions.assertEquals(productResponseDto.getProduct(), response.getProduct());
        Assertions.assertEquals(productResponseDto.getSerialNumber(), response.getSerialNumber());
        Assertions.assertEquals(productResponseDto.getQuantity(), response.getQuantity());
        Assertions.assertEquals(productResponseDto.getPrice(), response.getPrice());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName(value = "Devera atualizar a quantidade em estoque um produto no banco de dados")
    void mustBeUpdateStockQuantityProduct() {

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.of(Product.builder()
                .quantity(23)
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3049.44))
                .product("Cabo USB tipo C")
                .build()));

        Product responseProduct = Product.builder()
                .quantity(46)
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3049.44))
                .product("Cabo USB tipo C")
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(responseProduct);

        ProductResponseDTO response = productService.insertProduct(productRequestDto);

        Assertions.assertEquals(productResponseDto.getProduct(), response.getProduct());
        Assertions.assertEquals(productResponseDto.getSerialNumber(), response.getSerialNumber());
        Assertions.assertEquals(productResponseDto.getQuantity() * 2, response.getQuantity());
        Assertions.assertEquals(productResponseDto.getPrice(), response.getPrice());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName(value = "Devera buscar um produto pelo serialNumber")
    void mustBeReturnProductBySerialNumberSuccess() throws ProductNotFoundException {

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.findProductBySerialNumber(SERIAL_NUMBER);

        Assertions.assertEquals(productResponseDto.getProduct(), response.getProduct());
        Assertions.assertEquals(productResponseDto.getSerialNumber(), response.getSerialNumber());
        Assertions.assertEquals(productResponseDto.getQuantity(), response.getQuantity());
        Assertions.assertEquals(productResponseDto.getPrice(), response.getPrice());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Devera retornar um erro quando for buscar um produto pelo serialNumber e nao existir")
    void mustBeReturnProductBySerialNumberError() {

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());

        Throwable response = Assertions.assertThrows(ProductNotFoundException.class, () -> productService.findProductBySerialNumber(SERIAL_NUMBER));

        Assertions.assertEquals(ProductNotFoundException.class, response.getClass());
        Assertions.assertEquals("Produto nao localizado com o serialNumber informado", response.getMessage());

        verify(productRepository, times(1)).findBySerialNumber(anyString());

    }

    @Test
    @DisplayName(value = "Devera retornar atualizar um produto com sucesso")
    void mustBeUpdateProductSuccess() throws ProductNotFoundException {

        Product responseProduct = Product.builder()
                .quantity(46)
                .serialNumber(SERIAL_NUMBER)
                .price(BigDecimal.valueOf(3059.44))
                .product("Cabo USB tipo C")
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(responseProduct);

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.of(product));

        ProductResponseDTO response = productService.updateProductBySerialNumber(productRequestDto.getSerialNumber(),
                productRequestDto);

        Assertions.assertEquals(responseProduct.getProduct(), response.getProduct());
        Assertions.assertEquals(responseProduct.getSerialNumber(), response.getSerialNumber());
        Assertions.assertEquals(responseProduct.getQuantity(), response.getQuantity());
        Assertions.assertEquals(responseProduct.getPrice(), response.getPrice());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName(value = "Devera retornar um erro quando for atualizar um produto que nao existe pelo serialNumber e nao existir")
    void mustBeThrowExceptionProductNotFoundWhenUpdateNotExistProduct() {

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());

        Throwable response = Assertions.assertThrows(ProductNotFoundException.class,
                () -> productService.updateProductBySerialNumber(SERIAL_NUMBER, productRequestDto));

        Assertions.assertEquals(ProductNotFoundException.class, response.getClass());
        Assertions.assertEquals("Produto nao localizado com o serialNumber informado", response.getMessage());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    @DisplayName(value = "Devera retornar um Pageable com produtos com sucesso")
    void mustBeReturnPageProductResponseSuccess(){

        Pageable pageable = PageRequest.of(1, 10);
        Page<Product> productPage = new PageImpl<>(pageableResponseDto.getContent());

        when(productRepository.findAll(any(Pageable.class))).thenReturn(productPage);

        PageableResponseDTO pageableResponseDTO = productService.findAllProducts(pageable);

        Assertions.assertEquals(productResponseDto.getProduct(), pageableResponseDTO.getContent().get(0).getProduct());
        Assertions.assertEquals(productResponseDto.getSerialNumber(), pageableResponseDTO.getContent().get(0).getSerialNumber());
        Assertions.assertEquals(productResponseDto.getQuantity(), pageableResponseDTO.getContent().get(0).getQuantity());
        Assertions.assertEquals(productResponseDto.getPrice(), pageableResponseDTO.getContent().get(0).getPrice());
        Assertions.assertEquals(1, pageableResponseDTO.getContent().size());

        verify(productRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName(value = "Devera deletar um produto do banco de dados")
    void mustBeDeleteProductSuccess(){

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(any(Product.class));

        Assertions.assertDoesNotThrow(() -> productService.deleteProductBySerialNumber(SERIAL_NUMBER));
    }

    @Test
    @DisplayName(value = "Devera retornar um erro ProductNotFoundException quando o produto nao for encontrado")
    void mustBeThrownProductNotFoundExceptionWhenProductNotExist(){

        when(productRepository.findBySerialNumber(anyString())).thenReturn(Optional.empty());

        Throwable response = Assertions.assertThrows(ProductNotFoundException.class, () ->
                productService.deleteProductBySerialNumber(SERIAL_NUMBER));

        Assertions.assertEquals(ProductNotFoundException.class, response.getClass());
        Assertions.assertEquals("Produto nao localizado com o serialNumber informado", response.getMessage());

        verify(productRepository, times(1)).findBySerialNumber(anyString());
        verifyNoMoreInteractions(productRepository);
    }
}
