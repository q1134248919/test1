package com.family.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodoVO {
    private String id;
    private String title;
    private boolean done;
    private String creatorNickname;
    private String createTime;
}
