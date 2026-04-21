package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.ReservationCreateRequest;
import com.catcoffee.backend.entity.Reservation;
import com.catcoffee.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
@Tag(name = "预约管理")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasAuthority('reservation:read')")
    @Operation(summary = "分页查询预约")
    public ApiResponse<PageResult<Reservation>> list(@RequestParam(defaultValue = "1") Long current,
                                                     @RequestParam(defaultValue = "10") Long size,
                                                     @RequestParam(required = false) String status) {
        return ApiResponse.success(reservationService.list(current, size, status));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('reservation:write')")
    @Operation(summary = "新增或编辑预约")
    public ApiResponse<Reservation> save(@Valid @RequestBody ReservationCreateRequest request) {
        return ApiResponse.success("预约保存成功", reservationService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('reservation:delete')")
    @Operation(summary = "删除预约")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ApiResponse.success("预约删除成功", null);
    }
}
