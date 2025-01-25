package com.kai.test_practice.config;

import com.kai.test_practice.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 處理 UserNotFoundException
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
        return new ErrorResponse("User not found", HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    // 處理參數驗證失敗 (例如 @Valid 的驗證)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        // 提取所有錯誤訊息
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return new ErrorResponse("Validation failed", HttpStatus.BAD_REQUEST.toString(), errorMessage);

    }

    // 處理 IllegalArgumentException 或其他自訂業務邏輯錯誤
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("Bad request", HttpStatus.BAD_REQUEST.toString(), ex.getMessage());
    }

    // 處理其他未預期的異常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        return new ErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.toString(), ex.getMessage());
    }

    // 定義統一的錯誤響應結構
    static class ErrorResponse {
        private String error;
        private String httpStatus;
        private String message;
        private String timestamp;

        public ErrorResponse(String error, String httpStatus, String message) {
            this.error = error;
            this.httpStatus = httpStatus;
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString(); // 增加時間戳
        }

        public String getError() {
            return error;
        }

        public String getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }
}
