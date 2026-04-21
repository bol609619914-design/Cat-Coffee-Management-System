package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.TableSaveRequest;
import com.catcoffee.backend.entity.CafeTable;
import com.catcoffee.backend.service.CafeTableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
@Tag(name = "桌台管理")
public class CafeTableController {

    private final CafeTableService cafeTableService;

    @GetMapping
    @PreAuthorize("hasAuthority('table:read')")
    @Operation(summary = "分页查询桌台")
    public ApiResponse<PageResult<CafeTable>> list(@RequestParam(defaultValue = "1") Long current,
                                                   @RequestParam(defaultValue = "10") Long size,
                                                   @RequestParam(required = false) String status) {
        return ApiResponse.success(cafeTableService.list(current, size, status));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('table:write')")
    @Operation(summary = "新增或编辑桌台")
    public ApiResponse<CafeTable> save(@Valid @RequestBody TableSaveRequest request) {
        return ApiResponse.success("桌台保存成功", cafeTableService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('table:delete')")
    @Operation(summary = "删除桌台")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        cafeTableService.delete(id);
        return ApiResponse.success("桌台删除成功", null);
    }
}
