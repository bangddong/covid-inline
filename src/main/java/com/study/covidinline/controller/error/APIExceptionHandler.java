package com.study.covidinline.controller.error;

import com.study.covidinline.constant.ErrorCode;
import com.study.covidinline.dto.APIErrorResponse;
import com.study.covidinline.exception.GeneralException;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice(annotations = {RestController.class, RepositoryRestController.class})
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        return handleExceptionInternal(
                e, ErrorCode.VALIDATION_ERROR, HttpHeaders.EMPTY, request
        );
    }

    @ExceptionHandler
    public ResponseEntity<Object> general(GeneralException e, WebRequest request) {
        return handleExceptionInternal(e, e.getErrorCode(), HttpHeaders.EMPTY, request);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        return handleExceptionInternal(e, ErrorCode.INTERNAL_ERROR, HttpHeaders.EMPTY, request);
    }

    /**
     * 상속받은 기본 ResponseEntityExceptionHandler 는 사용하되
     * 다 NULL 인 body 부만 커스텀하기위해 Override.
     */
   @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
       return handleExceptionInternal(ex, ErrorCode.valueOf(status), headers, status, request);
   }

    private ResponseEntity<Object> handleExceptionInternal(
            Exception e, ErrorCode errorCode, HttpHeaders headers, WebRequest request
    ) {
        return super.handleExceptionInternal(
                e,
                APIErrorResponse.of(false, errorCode.getCode(), errorCode.getMessage(e)),
                headers,
                errorCode.getHttpStatus(),
                request
        );
    }
}
