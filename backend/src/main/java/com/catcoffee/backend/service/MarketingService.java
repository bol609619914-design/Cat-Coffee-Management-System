package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CouponIssueRequest;
import com.catcoffee.backend.dto.CouponTemplateSaveRequest;
import com.catcoffee.backend.dto.MarketingActivitySaveRequest;
import com.catcoffee.backend.dto.ReviewSaveRequest;
import com.catcoffee.backend.entity.CouponTemplate;
import com.catcoffee.backend.entity.MemberPointFlow;
import com.catcoffee.backend.vo.MarketingActivityVO;
import com.catcoffee.backend.vo.MemberPointSummaryVO;
import com.catcoffee.backend.vo.OrderReviewVO;
import com.catcoffee.backend.vo.ReviewOptionVO;
import com.catcoffee.backend.vo.UserCouponVO;

import java.util.List;

public interface MarketingService {

    MemberPointSummaryVO currentPointSummary();

    PageResult<MemberPointFlow> pointFlows(long current, long size, Long userId, String changeType);

    PageResult<CouponTemplate> couponTemplates(long current, long size, String keyword, Integer status);

    CouponTemplate saveCouponTemplate(CouponTemplateSaveRequest request);

    void deleteCouponTemplate(Long id);

    void issueCoupon(Long templateId, CouponIssueRequest request);

    void receiveCoupon(Long templateId);

    PageResult<UserCouponVO> userCoupons(long current, long size, Long userId, String status);

    PageResult<OrderReviewVO> reviews(long current, long size, Long orderId, Integer rating);

    OrderReviewVO saveReview(ReviewSaveRequest request);

    void deleteReview(Long id);

    List<ReviewOptionVO> reviewOptions();

    PageResult<MarketingActivityVO> activities(long current, long size, Integer status, String activityType);

    MarketingActivityVO saveActivity(MarketingActivitySaveRequest request);

    void deleteActivity(Long id);
}
