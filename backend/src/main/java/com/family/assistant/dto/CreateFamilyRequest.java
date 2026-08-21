package com.family.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateFamilyRequest {
    @NotBlank(message = "家庭名称不能为空")
    @Size(max = 20, message = "家庭名称最多 20 字")
    private String name;
}
