package com.health.module.resident.dto;

import lombok.Data;

@Data
public class ResidentQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String name;
    private String idCard;
    private String phone;
    private Integer gender;
}
