package com.health.module.followup.controller;

import com.alibaba.excel.EasyExcel;
import com.health.common.PageResult;
import com.health.common.Result;
import com.health.module.followup.dto.FollowupQueryDTO;
import com.health.module.followup.entity.FollowupRecord;
import com.health.module.followup.service.FollowupRecordService;
import com.health.module.followup.vo.FollowupVO;
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

@Tag(name = "随访记录管理")
@RestController
@RequestMapping("/api/followup-records")
@RequiredArgsConstructor
public class FollowupRecordController {

    private final FollowupRecordService service;

    @Operation(summary = "随访分页列表")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<PageResult<FollowupVO>> page(FollowupQueryDTO dto) {
        return Result.ok(PageResult.of(service.page(dto)));
    }

    @Operation(summary = "按居民ID查询随访列表")
    @GetMapping("/resident/{residentId}")
    public Result<List<FollowupRecord>> listByResident(@PathVariable Long residentId) {
        return Result.ok(service.listByResident(residentId));
    }

    @Operation(summary = "新增随访")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> create(@RequestBody FollowupRecord record) {
        service.create(record);
        return Result.ok();
    }

    @Operation(summary = "修改随访")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> update(@PathVariable Long id, @RequestBody FollowupRecord record) {
        service.update(id, record);
        return Result.ok();
    }

    @Operation(summary = "删除随访")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public Result<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return Result.ok();
    }

    @Operation(summary = "导出随访记录 Excel")
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    public void export(FollowupQueryDTO dto, HttpServletResponse response) throws IOException {
        dto.setPageSize(10000);
        List<FollowupVO> list = service.page(dto).getRecords();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String filename = URLEncoder.encode("随访记录", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + filename + ".xlsx");
        EasyExcel.write(response.getOutputStream(), FollowupVO.class).sheet("随访记录").doWrite(list);
    }
}
