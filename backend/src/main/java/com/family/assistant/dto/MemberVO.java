package com.family.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
    private String id;
    private String nickname;
    private String username;
    private boolean owner;
}
