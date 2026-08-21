package com.family.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTodoRequest {
    @NotBlank(message = "待办内容不能为空")
    @Size(max = 100, message = "待办最多 100 字")
    private String title;
}
