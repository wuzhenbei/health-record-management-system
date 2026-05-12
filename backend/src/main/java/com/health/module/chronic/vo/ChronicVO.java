package com.health.module.chronic.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ChronicVO {
    private Long id;
    private Long residentId;
    private String residentName;
    private String idCard;
    private String diseaseType;
    private LocalDate confirmDate;
    private String status;
    private String level;
    private String remark;
    private LocalDateTime createTime;
}
