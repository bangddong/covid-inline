package com.study.covidinline.dto;

import com.study.covidinline.constant.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 공통 API response
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class APIDataResponse<T> extends APIErrorResponse{

    private final T data;

    private APIDataResponse(T data) {
        super(true, ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
        this.data = data;
    }

    public static <T> APIDataResponse<T> of(T data) {
        return new APIDataResponse<T>(data);
    }
    public static <T> APIDataResponse<T> empty() {
        return new APIDataResponse<T>(null);
    }
}
