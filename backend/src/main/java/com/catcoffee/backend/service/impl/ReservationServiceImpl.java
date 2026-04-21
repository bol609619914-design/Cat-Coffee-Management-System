package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.ReservationCreateRequest;
import com.catcoffee.backend.entity.CafeTable;
import com.catcoffee.backend.entity.Reservation;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.CafeTableMapper;
import com.catcoffee.backend.mapper.ReservationMapper;
import com.catcoffee.backend.security.AuthUser;
import com.catcoffee.backend.security.SecurityUtils;
import com.catcoffee.backend.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final CafeTableMapper cafeTableMapper;

    @Override
    public PageResult<Reservation> list(long current, long size, String status) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Reservation::getStatus, status)
                .eq(isCustomerUser(currentUser), Reservation::getUserId, currentUser.getId())
                .orderByDesc(Reservation::getReservationTime);
        Page<Reservation> page = reservationMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation save(ReservationCreateRequest request) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        CafeTable cafeTable = cafeTableMapper.selectById(request.getTableId());
        if (cafeTable == null) {
            throw new BusinessException("预约桌台不存在");
        }
        if (request.getGuestCount() > cafeTable.getCapacity()) {
            throw new BusinessException("预约人数超过桌台可容纳上限");
        }
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(request, reservation);
        if (isCustomerUser(currentUser)) {
            reservation.setUserId(currentUser.getId());
            reservation.setCustomerName(currentUser.getNickname());
        } else if (!StringUtils.hasText(request.getCustomerName())) {
            throw new BusinessException("客户姓名不能为空");
        }
        if (request.getId() == null) {
            reservationMapper.insert(reservation);
        } else {
            Reservation exist = reservationMapper.selectById(request.getId());
            if (exist == null) {
                throw new BusinessException("预约记录不存在");
            }
            assertOwner(exist, currentUser);
            reservation.setId(request.getId());
            reservation.setUserId(exist.getUserId());
            if (isCustomerUser(currentUser)) {
                reservation.setCustomerName(exist.getCustomerName());
            }
            reservationMapper.updateById(reservation);
        }
        return reservationMapper.selectById(reservation.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        AuthUser currentUser = SecurityUtils.getCurrentUser();
        Reservation reservation = reservationMapper.selectById(id);
        if (reservation == null) {
            throw new BusinessException("预约记录不存在");
        }
        assertOwner(reservation, currentUser);
        reservationMapper.deleteById(id);
    }

    private boolean isCustomerUser(AuthUser currentUser) {
        return currentUser.getRoles() != null && currentUser.getRoles().contains("user");
    }

    private void assertOwner(Reservation reservation, AuthUser currentUser) {
        if (isCustomerUser(currentUser) && !currentUser.getId().equals(reservation.getUserId())) {
            throw new BusinessException("只能操作自己的预约记录");
        }
    }
}
