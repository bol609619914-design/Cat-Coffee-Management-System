package com.catcoffee.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewSaveRequest {

    private Long id;

    @NotNull(message = "订单不能为空")
    private Long orderId;

    @NotNull(message = "饮品不能为空")
    private Long drinkId;

    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最低为 1")
    @Max(value = 5, message = "评分最高为 5")
    private Integer rating;

    private String content;
}
