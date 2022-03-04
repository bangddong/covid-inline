package com.study.covidinline.constant;

import com.study.covidinline.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * 발생 예상되는 에러를 정의
 * 추가적으로 예상되는 에러는 여기서 추가 정의한다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // success case
    OK(0, HttpStatus.OK, "OK"),

    // client error
    BAD_REQUEST(10000, HttpStatus.BAD_REQUEST, "Bad request"),
    SPRING_BAD_REQUEST(10001, HttpStatus.BAD_REQUEST, "Spring-detected bad request"),
    VALIDATION_ERROR(10002, HttpStatus.BAD_REQUEST, "Validation error"),
    NOT_FOUND(10003, HttpStatus.BAD_REQUEST, "Requested resource is not found"),

    // server error
    INTERNAL_ERROR(20000, HttpStatus.INTERNAL_SERVER_ERROR, "Internal error"),
    SPRING_INTERNAL_ERROR(20001, HttpStatus.INTERNAL_SERVER_ERROR, "Spring-detected internal error"),
    DATA_ACCESS_ERROR(20002, HttpStatus.INTERNAL_SERVER_ERROR, "Data access error")
    ;

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public static ErrorCode valueOf(HttpStatus httpStatus) {
        if (httpStatus == null) throw new GeneralException("HttpStatus is null.");

        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getHttpStatus() == httpStatus)
                .findFirst()
                .orElseGet(() -> {
                    if (httpStatus.is4xxClientError()) return ErrorCode.BAD_REQUEST;
                    else if (httpStatus.is5xxServerError()) return ErrorCode.INTERNAL_ERROR;
                    else return ErrorCode.OK;
                });
    }

    // basic exception message
    public String getMessage(Throwable e) {
        return getMessage(this.getMessage() + " - " + e.getMessage());
    }

    // custom exception message
    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(getMessage());
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name(), this.getCode());
    }

}
