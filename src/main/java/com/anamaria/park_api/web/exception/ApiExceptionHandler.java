package com.anamaria.park_api.web.exception;

import com.anamaria.park_api.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.security.access.AccessDeniedException;



@Slf4j
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class) //erro para quando o usuario não tiver authorização, isso quando já existe segurança na aplicação
    public ResponseEntity<ErrorMessage> AccessDeniedException(AccessDeniedException e, HttpServletRequest request){

        log.error("Api Error - ", e);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)//não possui autorização para fazer a requisição
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.FORBIDDEN, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> methodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request, BindingResult result){

        log.error("Api Error - ", e);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)//não conseguiu completar o processamento da entidade
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.UNPROCESSABLE_ENTITY, "Campo(s) inavalido(s)", result));
    }

    @ExceptionHandler({UserNameUniqueViolationException.class, CpfUniqueViolationException.class, CodigoUniqueViolationException.class}) //essa @ExceptionHandler pode receber um array de exceções, caso alguma delas seja laçada elas seram capturadas nesse metodo
    public ResponseEntity<ErrorMessage> uniqueViolationException(RuntimeException e, HttpServletRequest request){

        log.error("Api Error - ", e);
        return ResponseEntity.status(HttpStatus.CONFLICT)//houve um conflito de informações
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.CONFLICT, e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessage> entityNotFoundException(RuntimeException e, HttpServletRequest request){

        log.error("Api Error - ", e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(PasswordInvalidException.class)
    public ResponseEntity<ErrorMessage> passwordInvalidException(PasswordInvalidException e, HttpServletRequest request){

        log.error("Api Error - ", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, e.getMessage()));
    }

    @ExceptionHandler(Exception.class) //erro refente ao relatorio
    public ResponseEntity<ErrorMessage> internalServeErroException(Exception e, HttpServletRequest request){
        ErrorMessage error = new ErrorMessage(request, HttpStatus.INTERNAL_SERVER_ERROR,HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        log.error("Internal serve error {} {} ", error, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }
}
