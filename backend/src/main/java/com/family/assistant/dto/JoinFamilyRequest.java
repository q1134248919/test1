package com.family.assistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JoinFamilyRequest {
    @NotBlank(message = "邀请码不能为空")
    @Size(min = 6, max = 20, message = "邀请码格式不正确")
    private String inviteCode;
}
