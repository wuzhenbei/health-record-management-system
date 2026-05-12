package com.health.module.medical.controller;

import com.health.common.Result;
import com.health.module.medical.entity.MedicalHistory;
import com.health.module.medical.service.MedicalHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "病史/就诊记录")
@RestController
@RequestMapping("/api/medical-histories")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService service;

    @Operation(summary = "按居民ID查询病史列表")
    @GetMapping("/resident/{residentId}")
    public Result<List<MedicalHistory>> listByResident(@PathVariable Long residentId) {
        return Result.ok(service.listByResident(residentId));
    }

    @Operation(summary = "新增病史")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> create(@RequestBody MedicalHistory history) {
        service.create(history);
        return Result.ok();
    }

    @Operation(summary = "修改病史")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> update(@PathVariable Long id, @RequestBody MedicalHistory history) {
        service.update(id, history);
        return Result.ok();
    }

    @Operation(summary = "删除病史")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }
}
