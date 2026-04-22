package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CouponIssueRequest;
import com.catcoffee.backend.dto.CouponTemplateSaveRequest;
import com.catcoffee.backend.dto.MarketingActivityRuleRequest;
import com.catcoffee.backend.dto.MarketingActivitySaveRequest;
import com.catcoffee.backend.dto.ReviewSaveRequest;
import com.catcoffee.backend.entity.CouponTemplate;
import com.catcoffee.backend.entity.CustomerOrder;
import com.catcoffee.backend.entity.CustomerOrderItem;
import com.catcoffee.backend.entity.Drink;
import com.catcoffee.backend.entity.MarketingActivity;
import com.catcoffee.backend.entity.MarketingActivityRule;
import com.catcoffee.backend.entity.MemberPointFlow;
import com.catcoffee.backend.entity.OrderReview;
import com.catcoffee.backend.entity.SysUser;
import com.catcoffee.backend.entity.UserCoupon;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.CouponTemplateMapper;
import com.catcoffee.backend.mapper.CustomerOrderItemMapper;
import com.catcoffee.backend.mapper.CustomerOrderMapper;
import com.catcoffee.backend.mapper.DrinkMapper;
import com.catcoffee.backend.mapper.MarketingActivityMapper;
import com.catcoffee.backend.mapper.MarketingActivityRuleMapper;
import com.catcoffee.backend.mapper.MemberPointFlowMapper;
import com.catcoffee.backend.mapper.OrderReviewMapper;
import com.catcoffee.backend.mapper.SysUserMapper;
import com.catcoffee.backend.mapper.UserCouponMapper;
import com.catcoffee.backend.security.AuthUser;
import com.catcoffee.backend.security.SecurityUtils;
import com.catcoffee.backend.service.MarketingService;
import com.catcoffee.backend.vo.MarketingActivityVO;
import com.catcoffee.backend.vo.MemberPointSummaryVO;
import com.catcoffee.backend.vo.OrderReviewVO;
import com.catcoffee.backend.vo.ReviewOptionVO;
import com.catcoffee.backend.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketingServiceImpl implements MarketingService {

    public static final String COUPON_STATUS_UNUSED = "未使用";
    public static final String COUPON_STATUS_USED = "已使用";
    public static final String COUPON_STATUS_EXPIRED = "已过期";

    private final SysUserMapper sysUserMapper;
    private final MemberPointFlowMapper memberPointFlowMapper;
    private final CouponTemplateMapper couponTemplateMapper;
    private final UserCouponMapper userCouponMapper;
    private final OrderReviewMapper orderReviewMapper;
    private final MarketingActivityMapper marketingActivityMapper;
    private final MarketingActivityRuleMapper marketingActivityRuleMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final CustomerOrderItemMapper customerOrderItemMapper;
    private final DrinkMapper drinkMapper;

    @Override
    public MemberPointSummaryVO currentPointSummary() {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        SysUser user = sysUserMapper.selectById(currentUser.getId());
        return new MemberPointSummaryVO(user.getId(), user.getNickname(), safePoints(user.getMemberPoints()));
    }

    @Override
    public PageResult<MemberPointFlow> pointFlows(long current, long size, Long userId, String changeType) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        LambdaQueryWrapper<MemberPointFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!isAdminOrStaff(currentUser), MemberPointFlow::getUserId, currentUser.getId())
                .eq(isAdminOrStaff(currentUser) && userId != null, MemberPointFlow::getUserId, userId)
                .eq(StringUtils.hasText(changeType), MemberPointFlow::getChangeType, changeType)
                .orderByDesc(MemberPointFlow::getCreateTime);
        return PageResult.of(memberPointFlowMapper.selectPage(new Page<>(current, size), wrapper));
    }

    @Override
    public PageResult<CouponTemplate> couponTemplates(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<CouponTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), CouponTemplate::getName, keyword)
                .eq(status != null, CouponTemplate::getStatus, status)
                .orderByDesc(CouponTemplate::getCreateTime);
        return PageResult.of(couponTemplateMapper.selectPage(new Page<>(current, size), wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CouponTemplate saveCouponTemplate(CouponTemplateSaveRequest request) {
        CouponTemplate entity = request.getId() == null ? new CouponTemplate() : couponTemplateMapper.selectById(request.getId());
        if (request.getId() != null && entity == null) {
            throw new BusinessException("优惠券模板不存在");
        }
        BeanUtils.copyProperties(request, entity);
        if (request.getId() == null) {
            entity.setIssuedCount(0);
            couponTemplateMapper.insert(entity);
        } else {
            couponTemplateMapper.updateById(entity);
        }
        return couponTemplateMapper.selectById(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCouponTemplate(Long id) {
        if (couponTemplateMapper.selectById(id) == null) {
            throw new BusinessException("优惠券模板不存在");
        }
        couponTemplateMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void issueCoupon(Long templateId, CouponIssueRequest request) {
        issueCouponInternal(templateId, request.getUserId(), "后台发放", false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveCoupon(Long templateId) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        issueCouponInternal(templateId, currentUser.getId(), "用户领取", true);
    }

    @Override
    public PageResult<UserCouponVO> userCoupons(long current, long size, Long userId, String status) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        refreshExpiredCoupons();
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!isAdminOrStaff(currentUser), UserCoupon::getUserId, currentUser.getId())
                .eq(isAdminOrStaff(currentUser) && userId != null, UserCoupon::getUserId, userId)
                .eq(StringUtils.hasText(status), UserCoupon::getStatus, status)
                .orderByDesc(UserCoupon::getCreateTime);
        Page<UserCoupon> page = userCouponMapper.selectPage(new Page<>(current, size), wrapper);
        List<UserCouponVO> records = page.getRecords().stream().map(this::buildUserCouponVO).toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    public PageResult<OrderReviewVO> reviews(long current, long size, Long orderId, Integer rating) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        LambdaQueryWrapper<OrderReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(!isAdminOrStaff(currentUser), OrderReview::getUserId, currentUser.getId())
                .eq(orderId != null, OrderReview::getOrderId, orderId)
                .eq(rating != null, OrderReview::getRating, rating)
                .orderByDesc(OrderReview::getCreateTime);
        Page<OrderReview> page = orderReviewMapper.selectPage(new Page<>(current, size), wrapper);
        List<OrderReviewVO> records = page.getRecords().stream().map(this::buildReviewVO).toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderReviewVO saveReview(ReviewSaveRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        CustomerOrder order = customerOrderMapper.selectById(request.getOrderId());
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!isAdminOrStaff(currentUser) && !currentUser.getId().equals(order.getUserId())) {
            throw new BusinessException("只能评价自己的订单");
        }
        if (!"已完成".equals(order.getOrderStatus())) {
            throw new BusinessException("只有已完成订单才可评价");
        }
        CustomerOrderItem orderItem = customerOrderItemMapper.selectOne(new LambdaQueryWrapper<CustomerOrderItem>()
                .eq(CustomerOrderItem::getOrderId, request.getOrderId())
                .eq(CustomerOrderItem::getDrinkId, request.getDrinkId())
                .last("LIMIT 1"));
        if (orderItem == null) {
            throw new BusinessException("订单中不存在该饮品");
        }

        OrderReview review = request.getId() == null ? new OrderReview() : orderReviewMapper.selectById(request.getId());
        if (request.getId() != null && review == null) {
            throw new BusinessException("评价不存在");
        }
        if (request.getId() == null) {
            OrderReview existing = orderReviewMapper.selectOne(new LambdaQueryWrapper<OrderReview>()
                    .eq(OrderReview::getUserId, currentUser.getId())
                    .eq(OrderReview::getOrderId, request.getOrderId())
                    .eq(OrderReview::getDrinkId, request.getDrinkId())
                    .last("LIMIT 1"));
            if (existing != null) {
                throw new BusinessException("该订单饮品已评价，无需重复提交");
            }
            review.setUserId(currentUser.getId());
            review.setStatus(1);
        }
        review.setOrderId(request.getOrderId());
        review.setDrinkId(request.getDrinkId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        if (request.getId() == null) {
            orderReviewMapper.insert(review);
        } else {
            orderReviewMapper.updateById(review);
        }
        return buildReviewVO(orderReviewMapper.selectById(review.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long id) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        OrderReview review = orderReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException("评价不存在");
        }
        if (!isAdminOrStaff(currentUser) && !currentUser.getId().equals(review.getUserId())) {
            throw new BusinessException("只能删除自己的评价");
        }
        orderReviewMapper.deleteById(id);
    }

    @Override
    public List<ReviewOptionVO> reviewOptions() {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        List<CustomerOrder> orders = customerOrderMapper.selectList(new LambdaQueryWrapper<CustomerOrder>()
                .eq(CustomerOrder::getUserId, currentUser.getId())
                .eq(CustomerOrder::getOrderStatus, "已完成")
                .orderByDesc(CustomerOrder::getCreateTime));
        List<ReviewOptionVO> options = new ArrayList<>();
        for (CustomerOrder order : orders) {
            List<CustomerOrderItem> items = customerOrderItemMapper.selectList(new LambdaQueryWrapper<CustomerOrderItem>()
                    .eq(CustomerOrderItem::getOrderId, order.getId()));
            for (CustomerOrderItem item : items) {
                OrderReview existing = orderReviewMapper.selectOne(new LambdaQueryWrapper<OrderReview>()
                        .eq(OrderReview::getUserId, currentUser.getId())
                        .eq(OrderReview::getOrderId, order.getId())
                        .eq(OrderReview::getDrinkId, item.getDrinkId())
                        .last("LIMIT 1"));
                if (existing == null) {
                    options.add(new ReviewOptionVO(order.getId(), order.getOrderNo(), item.getDrinkId(), item.getDrinkName()));
                }
            }
        }
        return options;
    }

    @Override
    public PageResult<MarketingActivityVO> activities(long current, long size, Integer status, String activityType) {
        LambdaQueryWrapper<MarketingActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(status != null, MarketingActivity::getStatus, status)
                .eq(StringUtils.hasText(activityType), MarketingActivity::getActivityType, activityType)
                .orderByDesc(MarketingActivity::getCreateTime);
        Page<MarketingActivity> page = marketingActivityMapper.selectPage(new Page<>(current, size), wrapper);
        List<MarketingActivityVO> records = page.getRecords().stream().map(this::buildActivityVO).toList();
        return new PageResult<>(page.getCurrent(), page.getSize(), page.getTotal(), records);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MarketingActivityVO saveActivity(MarketingActivitySaveRequest request) {
        MarketingActivity activity = request.getId() == null ? new MarketingActivity() : marketingActivityMapper.selectById(request.getId());
        if (request.getId() != null && activity == null) {
            throw new BusinessException("活动不存在");
        }
        BeanUtils.copyProperties(request, activity);
        if (request.getId() == null) {
            marketingActivityMapper.insert(activity);
        } else {
            marketingActivityMapper.updateById(activity);
            marketingActivityRuleMapper.delete(new LambdaQueryWrapper<MarketingActivityRule>().eq(MarketingActivityRule::getActivityId, activity.getId()));
        }
        saveActivityRules(activity.getId(), request.getRules());
        return buildActivityVO(marketingActivityMapper.selectById(activity.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        if (marketingActivityMapper.selectById(id) == null) {
            throw new BusinessException("活动不存在");
        }
        marketingActivityMapper.deleteById(id);
        marketingActivityRuleMapper.delete(new LambdaQueryWrapper<MarketingActivityRule>().eq(MarketingActivityRule::getActivityId, id));
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderBenefitResult applyOrderBenefits(Long userId, Integer pointsUsed, Long userCouponId, BigDecimal orderAmount) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return new OrderBenefitResult(BigDecimal.ZERO, null, 0);
        }
        BigDecimal discountAmount = BigDecimal.ZERO;
        UserCoupon userCoupon = null;
        int usedPoints = pointsUsed == null ? 0 : pointsUsed;
        if (usedPoints < 0) {
            throw new BusinessException("积分抵扣不能小于 0");
        }
        if (usedPoints > 0) {
            if (safePoints(user.getMemberPoints()) < usedPoints) {
                throw new BusinessException("会员积分不足");
            }
            discountAmount = discountAmount.add(BigDecimal.valueOf(usedPoints));
        }
        if (userCouponId != null) {
            refreshExpiredCoupons();
            userCoupon = userCouponMapper.selectById(userCouponId);
            if (userCoupon == null || !userId.equals(userCoupon.getUserId())) {
                throw new BusinessException("优惠券不存在");
            }
            if (!COUPON_STATUS_UNUSED.equals(userCoupon.getStatus())) {
                throw new BusinessException("优惠券当前不可用");
            }
            if (userCoupon.getExpireTime() != null && userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
                throw new BusinessException("优惠券已过期");
            }
            if (orderAmount.compareTo(userCoupon.getThresholdAmount()) < 0) {
                throw new BusinessException("当前订单未满足优惠券使用门槛");
            }
            discountAmount = discountAmount.add(userCoupon.getDiscountAmount());
        }
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }
        return new OrderBenefitResult(discountAmount, userCoupon, usedPoints);
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeOrderBenefits(CustomerOrder order, UserCoupon userCoupon, Integer pointsUsed) {
        if (order.getUserId() == null) {
            return;
        }
        if (pointsUsed != null && pointsUsed > 0) {
            changePoints(order.getUserId(), -pointsUsed, "使用", "订单", order.getId(), "订单抵扣积分");
        }
        if (userCoupon != null) {
            userCoupon.setStatus(COUPON_STATUS_USED);
            userCoupon.setUsedTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            userCouponMapper.updateById(userCoupon);
        }
        if ("已支付".equals(order.getPayStatus()) && order.getPayableAmount() != null) {
            int award = order.getPayableAmount().setScale(0, RoundingMode.DOWN).intValue();
            if (award > 0) {
                changePoints(order.getUserId(), award, "发放", "订单", order.getId(), "订单支付奖励积分");
                order.setPointsAwarded(award);
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void rollbackOrderBenefits(CustomerOrder order) {
        if (order == null || order.getUserId() == null) {
            return;
        }
        if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
            changePoints(order.getUserId(), order.getPointsUsed(), "返还", "订单", order.getId(), "订单编辑返还积分");
        }
        if (order.getPointsAwarded() != null && order.getPointsAwarded() > 0) {
            changePoints(order.getUserId(), -order.getPointsAwarded(), "扣回", "订单", order.getId(), "订单编辑扣回奖励积分");
        }
        if (order.getUserCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(order.getUserCouponId());
            if (userCoupon != null) {
                userCoupon.setStatus(COUPON_STATUS_UNUSED);
                userCoupon.setUsedTime(null);
                userCoupon.setOrderId(null);
                userCouponMapper.updateById(userCoupon);
            }
        }
    }

    private void issueCouponInternal(Long templateId, Long userId, String sourceType, boolean deductPoints) {
        CouponTemplate template = couponTemplateMapper.selectById(templateId);
        if (template == null || template.getStatus() == null || template.getStatus() != 1) {
            throw new BusinessException("优惠券模板不存在或不可用");
        }
        if (template.getTotalCount() != null && template.getIssuedCount() != null && template.getIssuedCount() >= template.getTotalCount()) {
            throw new BusinessException("优惠券已领完");
        }
        if ("固定时间".equals(template.getValidityType())) {
            if (template.getStartTime() != null && LocalDateTime.now().isBefore(template.getStartTime())) {
                throw new BusinessException("优惠券尚未开始发放");
            }
            if (template.getEndTime() != null && LocalDateTime.now().isAfter(template.getEndTime())) {
                throw new BusinessException("优惠券已结束发放");
            }
        }
        if (deductPoints && template.getPointCost() != null && template.getPointCost() > 0) {
            changePoints(userId, -template.getPointCost(), "兑换", "优惠券", templateId, "积分兑换优惠券");
        }
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setTemplateId(templateId);
        userCoupon.setCouponName(template.getName());
        userCoupon.setThresholdAmount(template.getThresholdAmount());
        userCoupon.setDiscountAmount(template.getDiscountAmount());
        userCoupon.setStatus(COUPON_STATUS_UNUSED);
        userCoupon.setReceivedTime(LocalDateTime.now());
        userCoupon.setExpireTime(calculateCouponExpireTime(template));
        userCoupon.setSourceType(sourceType);
        userCouponMapper.insert(userCoupon);

        template.setIssuedCount((template.getIssuedCount() == null ? 0 : template.getIssuedCount()) + 1);
        couponTemplateMapper.updateById(template);
    }

    private LocalDateTime calculateCouponExpireTime(CouponTemplate template) {
        if ("固定天数".equals(template.getValidityType())) {
            int days = template.getValidDays() == null ? 7 : template.getValidDays();
            return LocalDateTime.now().plusDays(days);
        }
        return template.getEndTime();
    }

    private void refreshExpiredCoupons() {
        List<UserCoupon> coupons = userCouponMapper.selectList(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getStatus, COUPON_STATUS_UNUSED)
                .isNotNull(UserCoupon::getExpireTime)
                .lt(UserCoupon::getExpireTime, LocalDateTime.now()));
        coupons.forEach(item -> {
            item.setStatus(COUPON_STATUS_EXPIRED);
            userCouponMapper.updateById(item);
        });
    }

    private void saveActivityRules(Long activityId, List<MarketingActivityRuleRequest> rules) {
        if (rules == null) {
            return;
        }
        for (MarketingActivityRuleRequest rule : rules) {
            MarketingActivityRule entity = new MarketingActivityRule();
            entity.setActivityId(activityId);
            entity.setRuleType(rule.getRuleType());
            entity.setRuleValue(rule.getRuleValue());
            entity.setTargetType(rule.getTargetType());
            entity.setTargetId(rule.getTargetId());
            entity.setSortOrder(rule.getSortOrder());
            marketingActivityRuleMapper.insert(entity);
        }
    }

    private UserCouponVO buildUserCouponVO(UserCoupon item) {
        SysUser user = sysUserMapper.selectById(item.getUserId());
        return new UserCouponVO(
                item.getId(),
                item.getUserId(),
                user == null ? "-" : user.getUsername(),
                item.getCouponName(),
                item.getThresholdAmount(),
                item.getDiscountAmount(),
                item.getStatus(),
                item.getReceivedTime(),
                item.getUsedTime(),
                item.getExpireTime(),
                item.getSourceType()
        );
    }

    private OrderReviewVO buildReviewVO(OrderReview review) {
        SysUser user = sysUserMapper.selectById(review.getUserId());
        CustomerOrder order = customerOrderMapper.selectById(review.getOrderId());
        Drink drink = drinkMapper.selectById(review.getDrinkId());
        return new OrderReviewVO(
                review.getId(),
                review.getUserId(),
                user == null ? "-" : user.getNickname(),
                review.getOrderId(),
                order == null ? "-" : order.getOrderNo(),
                review.getDrinkId(),
                drink == null ? "-" : drink.getName(),
                review.getRating(),
                review.getContent(),
                review.getStatus(),
                review.getCreateTime()
        );
    }

    private MarketingActivityVO buildActivityVO(MarketingActivity activity) {
        List<MarketingActivityRule> rules = marketingActivityRuleMapper.selectList(new LambdaQueryWrapper<MarketingActivityRule>()
                .eq(MarketingActivityRule::getActivityId, activity.getId())
                .orderByAsc(MarketingActivityRule::getSortOrder)
                .orderByAsc(MarketingActivityRule::getId));
        return new MarketingActivityVO(
                activity.getId(),
                activity.getName(),
                activity.getActivityType(),
                activity.getBannerImage(),
                activity.getStartTime(),
                activity.getEndTime(),
                activity.getStatus(),
                activity.getDescription(),
                rules
        );
    }

    private void changePoints(Long userId, Integer delta, String changeType, String bizType, Long bizId, String remark) {
        if (delta == null || delta == 0) {
            return;
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        int currentPoints = safePoints(user.getMemberPoints());
        int targetPoints = currentPoints + delta;
        if (targetPoints < 0) {
            throw new BusinessException("会员积分不足");
        }
        user.setMemberPoints(targetPoints);
        sysUserMapper.updateById(user);

        MemberPointFlow flow = new MemberPointFlow();
        flow.setUserId(userId);
        flow.setChangeType(changeType);
        flow.setChangeAmount(delta);
        flow.setBalanceAfter(targetPoints);
        flow.setBizType(bizType);
        flow.setBizId(bizId);
        flow.setRemark(remark);
        memberPointFlowMapper.insert(flow);
    }

    private boolean isAdminOrStaff(AuthUser currentUser) {
        List<String> roles = currentUser.getRoles();
        return roles != null && (roles.contains("admin") || roles.contains("staff"));
    }

    private int safePoints(Integer points) {
        return points == null ? 0 : points;
    }

    public record OrderBenefitResult(BigDecimal discountAmount, UserCoupon userCoupon, Integer pointsUsed) {
    }
}
