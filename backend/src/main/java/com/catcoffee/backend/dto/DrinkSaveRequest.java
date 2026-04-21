package com.catcoffee.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrinkSaveRequest {

    private Long id;

    @NotBlank(message = "饮品名称不能为空")
    private String name;

    @NotBlank(message = "分类不能为空")
    private String category;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能小于 0")
    private BigDecimal price;

    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能小于 0")
    private Integer stock;

    private Integer isRecommended;
    private String imageUrl;
    private String description;
    private Integer status;
}
