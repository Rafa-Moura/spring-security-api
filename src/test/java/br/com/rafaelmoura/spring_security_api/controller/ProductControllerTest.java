package br.com.rafaelmoura.spring_security_api.controller;

import br.com.rafaelmoura.spring_security_api.exceptions.ProductNotFoundException;
import br.com.rafaelmoura.spring_security_api.model.dto.PageableResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductRequestDTO;
import br.com.rafaelmoura.spring_security_api.model.dto.ProductResponseDTO;
import br.com.rafaelmoura.spring_security_api.model.entity.Product;
import br.com.rafaelmoura.spring_security_api.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    public static final String API_VERSION = "/v1";
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ProductService productService;
    @InjectMocks
    ProductController productController;

    PageableResponseDTO pageableResponseDto;
    ProductResponseDTO productResponseDto;
    ProductRequestDTO productRequestDto;
    private final String URL_BASE = "http://localhost:8080/api/products";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productRequestDto = ProductRequestDTO.builder()
                .product("Notebook Dell Latitude 3451")
                .price(BigDecimal.valueOf(3049.44))
                .quantity(23)
                .build();

        productResponseDto = ProductResponseDTO.builder()
                .quantity(10)
                .serialNumber("AAASD93847")
                .price(BigDecimal.valueOf(23.44))
                .product("Cabo USB tipo C")
                .build();

        pageableResponseDto = PageableResponseDTO.builder()
                .content(List.of(Product.builder()
                        .quantity(10)
                        .serialNumber("AAASD93847")
                        .price(BigDecimal.valueOf(23.44))
                        .product("Cabo USB tipo C")
                        .build()))
                .totalPages(1)
                .firstPage(true)
                .lastPage(true)
                .pageNumber(0)
                .totalRecords(1L)
                .build();
    }

    @Test
    @DisplayName(value = "Deverá retornar uma lista de produtos cadastrados no sistema e status code 200")
    void mustBeReturnProductListAndStatusCode200() throws Exception {

        when(productService.findAllProducts(any())).thenReturn(pageableResponseDto);

        mockMvc.perform(get(URL_BASE.concat(API_VERSION))
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].serialNumber").value(pageableResponseDto.getContent().get(0).getSerialNumber()))
                .andExpect(jsonPath("$.content[0].quantity").value(pageableResponseDto.getContent().get(0).getQuantity()))
                .andExpect(jsonPath("$.content[0].price").value(pageableResponseDto.getContent().get(0).getPrice()))
                .andExpect(jsonPath("$.content[0].product").value(pageableResponseDto.getContent().get(0).getProduct()))
                .andDo(print());

        verify(productService, times(1)).findAllProducts(any());
    }

    @Test
    @DisplayName(value = "Deverá retornar um único produto cadastrado no sistema e status code 200")
    void mustBeReturnProductResponseDtoAndStatusCode200() throws Exception {

        when(productService.findProductBySerialNumber(anyString())).thenReturn(productResponseDto);

        mockMvc.perform(get(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value(productResponseDto.getSerialNumber()))
                .andExpect(jsonPath("$.quantity").value(productResponseDto.getQuantity()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.product").value(productResponseDto.getProduct()))
                .andDo(print());

        verify(productService, times(1)).findProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá retornar um erro ao tentar localizar um produto não existente no banco de dados com status code 404")
    void mustBeReturnProductNotFoundExceptionAndStatusCode404() throws Exception {

        when(productService.findProductBySerialNumber(anyString()))
                .thenThrow(new ProductNotFoundException("Produto nao localizado com o serialNumber informado"));

        mockMvc.perform(get(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Produto nao localizado com o serialNumber informado"))
                .andDo(print());

        verify(productService, times(1)).findProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá retornar um erro ao tentar localizar um produto e receber um erro interno com status code 500")
    void mustBeReturnSystemExceptionAndStatusCode500() throws Exception {

        when(productService.findProductBySerialNumber(anyString()))
                .thenThrow(new RuntimeException("Um erro genérico"));

        mockMvc.perform(get(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro interno, tente novamente mais tarde ou contate um administrador"))
                .andDo(print());

        verify(productService, times(1)).findProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá realizar a inserção de um novo produto no banco de dados e status code 201")
    void mustBeInsertNewProductAndReturnStatusCode201() throws Exception {

        when(productService.insertProduct(any(ProductRequestDTO.class))).thenReturn(productResponseDto);

        mockMvc.perform(post(URL_BASE.concat(API_VERSION))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(productRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialNumber").value(productResponseDto.getSerialNumber()))
                .andExpect(jsonPath("$.quantity").value(productResponseDto.getQuantity()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.product").value(productResponseDto.getProduct()))
                .andDo(print());

        verify(productService, times(1)).insertProduct(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName(value = "Deverá remover um único produto cadastrado no sistema e retornar status code 204")
    void mustBeDeleteAndStatusCode204() throws Exception {

        doNothing().when(productService).deleteProductBySerialNumber(anyString());

        mockMvc.perform(delete(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andDo(print());

        verify(productService, times(1)).deleteProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá retornar um erro ao tentar remover um produto não existente no banco de dados com status code 404")
    void mustBeReturnNotFoundExceptionAndStatusCode404WhenDeleteProduct() throws Exception {

        doThrow(new ProductNotFoundException("Produto nao localizado com o serialNumber informado"))
                .when(productService).deleteProductBySerialNumber(anyString());

        mockMvc.perform(delete(URL_BASE.concat(API_VERSION) + "/AAASD93822")
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Produto nao localizado com o serialNumber informado"))
                .andDo(print());

        verify(productService, times(1)).deleteProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá retornar um erro ao remover um produto e receber um erro interno com status code 500")
    void mustBeReturnSystemExceptionAndStatusCode500WhenDeleteProduct() throws Exception {

        doThrow(new RuntimeException()).when(productService).deleteProductBySerialNumber(anyString());

        mockMvc.perform(delete(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()))
                .andExpect(jsonPath("$.message").value("Ocorreu um erro interno, tente novamente mais tarde ou contate um administrador"))
                .andDo(print());

        verify(productService, times(1)).deleteProductBySerialNumber(anyString());
    }

    @Test
    @DisplayName(value = "Deverá atualizar um produto no banco de dados e status code 201")
    void mustBeUpdateProductAndReturnStatusCode201() throws Exception {

        ProductResponseDTO responseDTO = productResponseDto;
        responseDTO.setQuantity(productResponseDto.getQuantity() + 2);

        when(productService.updateProductBySerialNumber(anyString(), any(ProductRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put(URL_BASE.concat(API_VERSION).concat("/AAASD93847"))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(productRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialNumber").value(responseDTO.getSerialNumber()))
                .andExpect(jsonPath("$.quantity").value(responseDTO.getQuantity()))
                .andExpect(jsonPath("$.price").value(responseDTO.getPrice()))
                .andExpect(jsonPath("$.product").value(responseDTO.getProduct()))
                .andDo(print());

        verify(productService, times(1)).updateProductBySerialNumber(anyString(), any(ProductRequestDTO.class));
    }
}
