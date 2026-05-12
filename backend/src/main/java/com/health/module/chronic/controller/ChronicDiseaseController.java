package com.health.module.chronic.controller;

import com.alibaba.excel.EasyExcel;
import com.health.common.PageResult;
import com.health.common.Result;
import com.health.module.chronic.dto.ChronicQueryDTO;
import com.health.module.chronic.entity.ChronicDisease;
import com.health.module.chronic.service.ChronicDiseaseService;
import com.health.module.chronic.vo.ChronicVO;
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

@Tag(name = "慢病管理")
@RestController
@RequestMapping("/api/chronic-diseases")
@RequiredArgsConstructor
public class ChronicDiseaseController {

    private final ChronicDiseaseService service;

    @Operation(summary = "慢病分页列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<PageResult<ChronicVO>> page(ChronicQueryDTO dto) {
        return Result.ok(PageResult.of(service.page(dto)));
    }

    @Operation(summary = "按居民ID查询慢病")
    @GetMapping("/resident/{residentId}")
    public Result<List<ChronicDisease>> listByResident(@PathVariable Long residentId) {
        return Result.ok(service.listByResident(residentId));
    }

    @Operation(summary = "新增慢病")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> create(@RequestBody ChronicDisease chronic) {
        service.create(chronic);
        return Result.ok();
    }

    @Operation(summary = "修改慢病")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> update(@PathVariable Long id, @RequestBody ChronicDisease chronic) {
        service.update(id, chronic);
        return Result.ok();
    }

    @Operation(summary = "删除慢病")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    @Operation(summary = "导出慢病 Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public void export(ChronicQueryDTO dto, HttpServletResponse response) throws IOException {
        dto.setPageSize(10000);
        List<ChronicVO> list = service.page(dto).getRecords();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode("慢病管理", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ChronicVO.class).sheet("慢病管理").doWrite(list);
    }
}
