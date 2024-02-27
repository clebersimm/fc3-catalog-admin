package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = DomainException.class)
    public ResponseEntity<?> handleDomainException(
            final DomainException ex
    /* Pode ser acrecentado se quiser modificar os dados do retorno ou melhorar os logs
     * final HttpServletRequest req,
     * final HttpServletResponse res
     */
    ) {
        return ResponseEntity.unprocessableEntity().body(ApiError.from(ex));
    }

    record ApiError(String message, List<Error> errors) {
        static ApiError from(final DomainException ex) {
            return new ApiError(ex.getMessage(), ex.getErrors());
        }
    }
}
