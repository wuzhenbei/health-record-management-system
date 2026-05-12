package com.health.module.followup.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class FollowupVO {
    private Long id;
    private Long residentId;
    private String residentName;
    private Long chronicId;
    private String diseaseType;
    private LocalDate followupDate;
    private Long doctorId;
    private String doctorName;
    private String followupType;
    private String bp;
    private BigDecimal bloodSugar;
    private String symptom;
    private String advice;
    private LocalDate nextDate;
    private LocalDateTime createTime;
}
