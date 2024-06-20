package br.com.rafaelmoura.spring_security_api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage"})
public class SystemException extends GenericException{
    private final String code;

    public SystemException(String message, String code) {
        super(message);
        this.code = code;
    }
}
