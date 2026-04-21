package com.catcoffee.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardVO {

    private Long catCount;
    private Long reservationCount;
    private Long activeOrderCount;
    private BigDecimal todayRevenue;
    private List<NameValueVO> hotDrinks;
    private List<NameValueVO> reservationStatusSummary;
}
