package com.warba.assessment.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;

    private String message;

    private T payload;

    private int code;

    private List<ErrorDetail> errors;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String error;
        private String code;

        public ErrorDetail(String error) {
            this.error = error;
        }

        public ErrorDetail(String error, String code) {
            this.error = error;
            this.code = code;
        }
    }

    public static <T> ApiResponse<T> ok(T payload, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .payload(payload)
                .code(HttpStatus.OK.value())
                .build();
    }

    public static <T> ApiResponse<T> ok(T payload) {
        return ApiResponse.<T>builder()
                .success(true)
                .payload(payload)
                .code(HttpStatus.OK.value())
                .build();
    }

    public static <T> ApiResponse<T> created(T payload, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .payload(payload)
                .code(HttpStatus.CREATED.value())
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, Errors errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .payload(null)
                .code(HttpStatus.BAD_REQUEST.value())
                .errors(errors.getAllErrors().stream()
                        .map(e -> new ErrorDetail(e.getDefaultMessage(), e.getCode()))
                        .toList()
                ).build();
    }


}
