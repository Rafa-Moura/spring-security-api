package br.com.rafaelmoura.spring_security_api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericException> systemExceptionHandler(Exception ex){
        GenericException genericException = new GenericException("Ocorreu um erro interno, tente novamente mais tarde ou contate um administrador"
                , HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        return new ResponseEntity<>(genericException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<GenericException> productNotFoundExceptionHandler(ProductNotFoundException ex){
        GenericException genericException = new GenericException(ex.getMessage(),
                HttpStatus.NOT_FOUND.getReasonPhrase());

        return new ResponseEntity<>(genericException, HttpStatus.NOT_FOUND);
    }

}
