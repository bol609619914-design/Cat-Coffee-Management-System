package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.OrderCreateRequest;
import com.catcoffee.backend.entity.CustomerOrder;
import com.catcoffee.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "订单管理")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAuthority('order:read')")
    @Operation(summary = "分页查询订单")
    public ApiResponse<PageResult<CustomerOrder>> list(@RequestParam(defaultValue = "1") Long current,
                                                       @RequestParam(defaultValue = "10") Long size,
                                                       @RequestParam(required = false) String orderStatus,
                                                       @RequestParam(required = false) String payStatus) {
        return ApiResponse.success(orderService.list(current, size, orderStatus, payStatus));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('order:write')")
    @Operation(summary = "新增或编辑订单")
    public ApiResponse<CustomerOrder> save(@Valid @RequestBody OrderCreateRequest request) {
        return ApiResponse.success("订单保存成功", orderService.save(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('order:delete')")
    @Operation(summary = "删除订单")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return ApiResponse.success("订单删除成功", null);
    }
}
