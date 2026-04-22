package com.catcoffee.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CouponIssueRequest {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;
}
