package br.com.rafaelmoura.spring_security_api.model.dto;

import br.com.rafaelmoura.spring_security_api.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PageableResponseDTO {

    private List<Product> content;
    private int pageNumber;
    private int totalPages;
    private Long totalRecords;
    private boolean firstPage;
    private boolean lastPage;

}
