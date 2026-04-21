package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.service.DashboardService;
import com.catcoffee.backend.vo.DashboardVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "经营看板")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAuthority('dashboard:view')")
    @Operation(summary = "获取经营看板")
    public ApiResponse<DashboardVO> dashboard() {
        return ApiResponse.success(dashboardService.dashboard());
    }
}
