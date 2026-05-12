package com.health.module.medical.service;

import com.health.module.medical.entity.MedicalHistory;

import java.util.List;

public interface MedicalHistoryService {
    List<MedicalHistory> listByResident(Long residentId);
    void create(MedicalHistory history);
    void update(Long id, MedicalHistory history);
    void delete(Long id);
}
