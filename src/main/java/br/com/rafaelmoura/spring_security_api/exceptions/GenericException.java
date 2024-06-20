package br.com.rafaelmoura.spring_security_api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties({"stackTrace", "cause", "suppressed", "localizedMessage"})
public class GenericException extends Exception{
    private String code;
    public GenericException(String message, String code){
        super(message);
        this.code = code;
    }
    public GenericException(String message){
        super(message);
    }

}
