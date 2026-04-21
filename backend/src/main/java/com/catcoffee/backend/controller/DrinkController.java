package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.DrinkSaveRequest;
import com.catcoffee.backend.entity.Drink;
import com.catcoffee.backend.service.DrinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drinks")
@RequiredArgsConstructor
@Tag(name = "饮品管理")
public class DrinkController {

    private final DrinkService drinkService;

    @GetMapping
    @PreAuthorize("hasAuthority('drink:read')")
    @Operation(summary = "分页查询饮品")
    public ApiResponse<PageResult<Drink>> list(@RequestParam(defaultValue = "1") Long current,
                                               @RequestParam(defaultValue = "10") Long size,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Integer status) {
        return ApiResponse.success(drinkService.list(current, size, keyword, status));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('drink:write')")
    @Operation(summary = "新增或编辑饮品")
    public ApiResponse<Drink> save(@Valid @RequestBody DrinkSaveRequest request) {
        return ApiResponse.success("饮品保存成功", drinkService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('drink:delete')")
    @Operation(summary = "删除饮品")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        drinkService.delete(id);
        return ApiResponse.success("饮品删除成功", null);
    }
}
