package com.health.module.resident.controller;

import com.alibaba.excel.EasyExcel;
import com.health.common.BusinessException;
import com.health.common.PageResult;
import com.health.common.Result;
import com.health.module.resident.dto.ResidentQueryDTO;
import com.health.module.resident.entity.Resident;
import com.health.module.resident.service.ResidentService;
import com.health.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Tag(name = "居民档案管理")
@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    @Operation(summary = "居民档案分页列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<PageResult<Resident>> page(ResidentQueryDTO dto) {
        return Result.ok(PageResult.of(residentService.page(dto)));
    }

    @Operation(summary = "居民档案详情")
    @GetMapping("/{id}")
    public Result<Resident> getById(@PathVariable Long id) {
        if (SecurityUtil.isResident()) {
            Resident self = residentService.getByUserId(SecurityUtil.getCurrentUserId());
            if (self == null || !self.getId().equals(id)) {
                throw new BusinessException(403, "只能查看自己的档案");
            }
        }
        return Result.ok(residentService.getById(id));
    }

    @Operation(summary = "居民姓名快速搜索（供下拉选择）")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<List<Map<String, Object>>> search(@RequestParam(required = false, defaultValue = "") String name) {
        ResidentQueryDTO dto = new ResidentQueryDTO();
        dto.setName(name);
        dto.setPageSize(20);
        List<Map<String, Object>> result = residentService.page(dto).getRecords().stream().map(r -> {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("name", r.getName());
            m.put("idCard", r.getIdCard());
            return m;
        }).collect(java.util.stream.Collectors.toList());
        return Result.ok(result);
    }

    @Operation(summary = "获取当前居民自己的档案")
    @GetMapping("/my")
    @PreAuthorize("hasRole('RESIDENT')")
    public Result<Resident> my() {
        Resident r = residentService.getByUserId(SecurityUtil.getCurrentUserId());
        if (r == null) throw new BusinessException("档案不存在，请联系管理员");
        return Result.ok(r);
    }

    @Operation(summary = "新增居民档案")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> create(@RequestBody Resident resident) {
        residentService.create(resident);
        return Result.ok();
    }

    @Operation(summary = "修改居民档案")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> update(@PathVariable Long id, @RequestBody Resident resident) {
        residentService.update(id, resident);
        return Result.ok();
    }

    @Operation(summary = "删除居民档案")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        residentService.delete(id);
        return Result.ok();
    }

    @Operation(summary = "导出居民档案 Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public void export(ResidentQueryDTO dto, HttpServletResponse response) throws IOException {
        dto.setPageSize(10000);
        List<Resident> list = residentService.page(dto).getRecords();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode("居民档案", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream(), Resident.class).sheet("居民档案").doWrite(list);
    }

    @Operation(summary = "居民自助更新联系方式")
    @PutMapping("/my/contact")
    @PreAuthorize("hasRole('RESIDENT')")
    public Result<Void> updateContact(@RequestBody Map<String, String> body) {
        Resident r = residentService.getByUserId(SecurityUtil.getCurrentUserId());
        if (r == null) throw new BusinessException("档案不存在");
        if (body.containsKey("phone")) r.setPhone(body.get("phone"));
        if (body.containsKey("address")) r.setAddress(body.get("address"));
        residentService.update(r.getId(), r);
        return Result.ok();
    }
}
