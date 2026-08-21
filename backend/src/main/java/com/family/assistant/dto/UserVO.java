package com.family.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    private String id;
    private String username;
    private String nickname;
    private String avatar;
    private String phone;
    private String familyId;
}
