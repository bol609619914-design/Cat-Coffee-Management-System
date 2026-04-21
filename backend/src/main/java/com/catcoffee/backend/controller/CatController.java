package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CatSaveRequest;
import com.catcoffee.backend.entity.Cat;
import com.catcoffee.backend.service.CatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cats")
@RequiredArgsConstructor
@Tag(name = "猫咪管理")
public class CatController {

    private final CatService catService;

    @GetMapping
    @PreAuthorize("hasAuthority('cat:read')")
    @Operation(summary = "分页查询猫咪")
    public ApiResponse<PageResult<Cat>> list(@RequestParam(defaultValue = "1") Long current,
                                             @RequestParam(defaultValue = "10") Long size,
                                             @RequestParam(required = false) String keyword) {
        return ApiResponse.success(catService.list(current, size, keyword));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('cat:write')")
    @Operation(summary = "新增或编辑猫咪")
    public ApiResponse<Cat> save(@Valid @RequestBody CatSaveRequest request) {
        return ApiResponse.success("猫咪保存成功", catService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('cat:delete')")
    @Operation(summary = "删除猫咪")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        catService.delete(id);
        return ApiResponse.success("猫咪删除成功", null);
    }
}
