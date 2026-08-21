package com.family.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoryVO {
    private String id;
    private String url;
    private String caption;
    private String uploaderNickname;
    private String createTime;
}
