package com.family.assistant.dto;

import lombok.Data;

import java.util.List;

@Data
public class FamilyVO {
    private String id;
    private String name;
    private String inviteCode;
    private String ownerId;
    private boolean owner;
    private List<MemberVO> members;
}
