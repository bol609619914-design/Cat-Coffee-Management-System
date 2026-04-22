package com.catcoffee.backend.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberPointSummaryVO {

    private Long userId;
    private String nickname;
    private Integer currentPoints;
}
