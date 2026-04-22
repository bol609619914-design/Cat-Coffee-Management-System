package com.catcoffee.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderReviewVO {

    private Long id;
    private Long userId;
    private String nickname;
    private Long orderId;
    private String orderNo;
    private Long drinkId;
    private String drinkName;
    private Integer rating;
    private String content;
    private Integer status;
    private LocalDateTime createTime;
}
