package com.health.module.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String realName;
    private String phone;
    private String avatar;
    private String role;
    private Integer status;
}
