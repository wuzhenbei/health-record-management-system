package com.health.module.chronic.dto;

import lombok.Data;

@Data
public class ChronicQueryDTO {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String residentName;
    private String diseaseType;
    private String status;
}
