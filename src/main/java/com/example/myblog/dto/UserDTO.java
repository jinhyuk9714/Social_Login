package com.example.myblog.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
public class UserDTO {
    private String username;
    private String email;
    private Set<String> roles; // 사용자 역할 추가
}
