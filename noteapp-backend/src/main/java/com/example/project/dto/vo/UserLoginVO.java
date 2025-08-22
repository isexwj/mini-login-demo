package com.example.project.dto.vo;

import lombok.Data;

@Data
public class UserLoginVO {
    private Long userId;
    private String username;
    private String token;  // 添加 token 字段
}