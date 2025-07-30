package com.desafio.forohub.infra.errors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.xml.bind.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TratamientoErrores {
     @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<List<?>> error400Handler(MethodArgumentNotValidException e){
         var error = e.getFieldErrors().stream().map(validacionErroresDatos::new).toList();
                 return ResponseEntity.badRequest().body(error);
     }
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?>error404Handler(){
         return ResponseEntity.notFound().build();
    }
    @ExceptionHandler(IntegrityValidation.class)
    public ResponseEntity<String>errorHandlerIntegirtyValidation(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> errorHandlerBussinessValidation(Exception e){
         return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> invalidBodyHandler(Exception e){
        return ResponseEntity.badRequest().body("Algunas partes de la solicitud están expresadas de manera incorrecta...");
    }
    private record validacionErroresDatos(String field, String error){
         public validacionErroresDatos(FieldError error){
             this(error.getField(), error.getDefaultMessage());
         }

    }
}
