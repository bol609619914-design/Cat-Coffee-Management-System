package com.catcoffee.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    private Long id;

    private String customerName;

    private Long reservationId;

    @NotNull(message = "桌台不能为空")
    private Long tableId;

    @NotBlank(message = "支付状态不能为空")
    private String payStatus;

    @NotBlank(message = "订单状态不能为空")
    private String orderStatus;

    private Integer pointsUsed;

    private Long userCouponId;

    private String remark;

    @Valid
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderItemRequest> items;
}
