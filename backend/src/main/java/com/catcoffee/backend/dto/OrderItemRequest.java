package com.catcoffee.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderItemRequest {

    @NotNull(message = "饮品 ID 不能为空")
    private Long drinkId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为 1")
    private Integer quantity;
}
