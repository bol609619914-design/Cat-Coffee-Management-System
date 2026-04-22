package com.catcoffee.backend.controller;

import com.catcoffee.backend.common.ApiResponse;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CouponIssueRequest;
import com.catcoffee.backend.dto.CouponTemplateSaveRequest;
import com.catcoffee.backend.dto.MarketingActivitySaveRequest;
import com.catcoffee.backend.dto.ReviewSaveRequest;
import com.catcoffee.backend.entity.CouponTemplate;
import com.catcoffee.backend.entity.MemberPointFlow;
import com.catcoffee.backend.service.MarketingService;
import com.catcoffee.backend.vo.MarketingActivityVO;
import com.catcoffee.backend.vo.MemberPointSummaryVO;
import com.catcoffee.backend.vo.OrderReviewVO;
import com.catcoffee.backend.vo.ReviewOptionVO;
import com.catcoffee.backend.vo.UserCouponVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marketing")
@RequiredArgsConstructor
@Tag(name = "会员营销中心")
public class MarketingController {

    private final MarketingService marketingService;

    @GetMapping("/points/summary")
    @PreAuthorize("hasAuthority('points:read')")
    @Operation(summary = "查询当前积分概览")
    public ApiResponse<MemberPointSummaryVO> pointSummary() {
        return ApiResponse.success(marketingService.currentPointSummary());
    }

    @GetMapping("/points/flows")
    @PreAuthorize("hasAuthority('points:read')")
    @Operation(summary = "分页查询积分流水")
    public ApiResponse<PageResult<MemberPointFlow>> pointFlows(@RequestParam(defaultValue = "1") Long current,
                                                               @RequestParam(defaultValue = "10") Long size,
                                                               @RequestParam(required = false) Long userId,
                                                               @RequestParam(required = false) String changeType) {
        return ApiResponse.success(marketingService.pointFlows(current, size, userId, changeType));
    }

    @GetMapping("/coupons/templates")
    @PreAuthorize("hasAuthority('coupon:read')")
    @Operation(summary = "分页查询优惠券模板")
    public ApiResponse<PageResult<CouponTemplate>> couponTemplates(@RequestParam(defaultValue = "1") Long current,
                                                                   @RequestParam(defaultValue = "10") Long size,
                                                                   @RequestParam(required = false) String keyword,
                                                                   @RequestParam(required = false) Integer status) {
        return ApiResponse.success(marketingService.couponTemplates(current, size, keyword, status));
    }

    @PostMapping("/coupons/templates")
    @PreAuthorize("hasAuthority('coupon:write')")
    @Operation(summary = "新增或编辑优惠券模板")
    public ApiResponse<CouponTemplate> saveCouponTemplate(@Valid @RequestBody CouponTemplateSaveRequest request) {
        return ApiResponse.success("优惠券模板保存成功", marketingService.saveCouponTemplate(request));
    }

    @DeleteMapping("/coupons/templates/{id}")
    @PreAuthorize("hasAuthority('coupon:write')")
    @Operation(summary = "删除优惠券模板")
    public ApiResponse<Void> deleteCouponTemplate(@PathVariable Long id) {
        marketingService.deleteCouponTemplate(id);
        return ApiResponse.success("优惠券模板删除成功", null);
    }

    @PostMapping("/coupons/templates/{id}/issue")
    @PreAuthorize("hasAuthority('coupon:write')")
    @Operation(summary = "向指定用户发放优惠券")
    public ApiResponse<Void> issueCoupon(@PathVariable Long id, @Valid @RequestBody CouponIssueRequest request) {
        marketingService.issueCoupon(id, request);
        return ApiResponse.success("优惠券发放成功", null);
    }

    @PostMapping("/coupons/templates/{id}/receive")
    @PreAuthorize("hasAuthority('coupon:write')")
    @Operation(summary = "领取优惠券")
    public ApiResponse<Void> receiveCoupon(@PathVariable Long id) {
        marketingService.receiveCoupon(id);
        return ApiResponse.success("优惠券领取成功", null);
    }

    @GetMapping("/coupons/user")
    @PreAuthorize("hasAuthority('coupon:read')")
    @Operation(summary = "分页查询用户优惠券")
    public ApiResponse<PageResult<UserCouponVO>> userCoupons(@RequestParam(defaultValue = "1") Long current,
                                                             @RequestParam(defaultValue = "10") Long size,
                                                             @RequestParam(required = false) Long userId,
                                                             @RequestParam(required = false) String status) {
        return ApiResponse.success(marketingService.userCoupons(current, size, userId, status));
    }

    @GetMapping("/reviews")
    @PreAuthorize("hasAuthority('review:read')")
    @Operation(summary = "分页查询评价")
    public ApiResponse<PageResult<OrderReviewVO>> reviews(@RequestParam(defaultValue = "1") Long current,
                                                          @RequestParam(defaultValue = "10") Long size,
                                                          @RequestParam(required = false) Long orderId,
                                                          @RequestParam(required = false) Integer rating) {
        return ApiResponse.success(marketingService.reviews(current, size, orderId, rating));
    }

    @GetMapping("/reviews/options")
    @PreAuthorize("hasAuthority('review:write')")
    @Operation(summary = "查询可评价订单项")
    public ApiResponse<List<ReviewOptionVO>> reviewOptions() {
        return ApiResponse.success(marketingService.reviewOptions());
    }

    @PostMapping("/reviews")
    @PreAuthorize("hasAuthority('review:write')")
    @Operation(summary = "新增或编辑评价")
    public ApiResponse<OrderReviewVO> saveReview(@Valid @RequestBody ReviewSaveRequest request) {
        return ApiResponse.success("评价保存成功", marketingService.saveReview(request));
    }

    @DeleteMapping("/reviews/{id}")
    @PreAuthorize("hasAuthority('review:write')")
    @Operation(summary = "删除评价")
    public ApiResponse<Void> deleteReview(@PathVariable Long id) {
        marketingService.deleteReview(id);
        return ApiResponse.success("评价删除成功", null);
    }

    @GetMapping("/activities")
    @PreAuthorize("hasAuthority('activity:read')")
    @Operation(summary = "分页查询活动")
    public ApiResponse<PageResult<MarketingActivityVO>> activities(@RequestParam(defaultValue = "1") Long current,
                                                                   @RequestParam(defaultValue = "10") Long size,
                                                                   @RequestParam(required = false) Integer status,
                                                                   @RequestParam(required = false) String activityType) {
        return ApiResponse.success(marketingService.activities(current, size, status, activityType));
    }

    @PostMapping("/activities")
    @PreAuthorize("hasAuthority('activity:write')")
    @Operation(summary = "新增或编辑活动")
    public ApiResponse<MarketingActivityVO> saveActivity(@Valid @RequestBody MarketingActivitySaveRequest request) {
        return ApiResponse.success("活动保存成功", marketingService.saveActivity(request));
    }

    @DeleteMapping("/activities/{id}")
    @PreAuthorize("hasAuthority('activity:write')")
    @Operation(summary = "删除活动")
    public ApiResponse<Void> deleteActivity(@PathVariable Long id) {
        marketingService.deleteActivity(id);
        return ApiResponse.success("活动删除成功", null);
    }
}
