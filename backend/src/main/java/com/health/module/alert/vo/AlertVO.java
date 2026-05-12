package com.health.module.alert.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AlertVO {
    private Long id;
    private Long residentId;
    private String residentName;
    private String idCard;
    private String alertType;
    private String alertValue;
    private LocalDate measureDate;
    private Integer status;
    private String handleNote;
    private LocalDateTime createTime;
}
