package com.besttocode.doc.exceptions.handler;

import com.besttocode.doc.exceptions.DocDeleteException;
import com.besttocode.doc.exceptions.UserNotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler({DocDeleteException.class})
    public ResponseEntity<?> deleteDocByIdException(DocDeleteException exception, WebRequest request) {

        String bodyResponse = "No such document with that id! ";

        log.error(bodyResponse, exception);
        return handleExceptionInternal(exception, bodyResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

    }

    @ExceptionHandler({UserNotAllowedException.class})
    public ResponseEntity<?> userNotAllowedException(UserNotAllowedException exception, WebRequest request) {

        return handleExceptionInternal(exception, "You are not allowed to do this action!", new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<?> noSuchElementException(UserNotAllowedException exception, WebRequest request) {

        return handleExceptionInternal(exception, "You are not allowed to do this action!", new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }


}
