package com.catcoffee.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewOptionVO {

    private Long orderId;
    private String orderNo;
    private Long drinkId;
    private String drinkName;
}
