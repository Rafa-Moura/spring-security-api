package br.com.rafaelmoura.spring_security_api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage"})
public class ProductNotFoundException extends GenericException {
    private String code;

    public ProductNotFoundException(String message, String code) {
        super(message);
        this.code = code;
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
