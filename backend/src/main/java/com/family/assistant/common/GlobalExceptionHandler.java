package com.family.assistant.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("参数错误");
        return Result.fail(msg);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleTooLarge(MaxUploadSizeExceededException e) {
        return Result.fail("图片不能超过 8MB");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNotFound(NoResourceFoundException e) {
        return Result.fail(404, "接口不存在，请确认已重启后端");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleOther(Exception e) {
        e.printStackTrace();
        return Result.fail(500, e.getMessage() != null ? e.getMessage() : "服务器内部错误");
    }
}
