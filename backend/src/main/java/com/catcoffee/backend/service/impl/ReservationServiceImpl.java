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
        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Reservation::getStatus, status)
                .orderByDesc(Reservation::getReservationTime);
        Page<Reservation> page = reservationMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Reservation save(ReservationCreateRequest request) {
        CafeTable cafeTable = cafeTableMapper.selectById(request.getTableId());
        if (cafeTable == null) {
            throw new BusinessException("预约桌台不存在");
        }
        if (request.getGuestCount() > cafeTable.getCapacity()) {
            throw new BusinessException("预约人数超过桌台可容纳上限");
        }
        Reservation reservation = new Reservation();
        BeanUtils.copyProperties(request, reservation);
        if (request.getId() == null) {
            reservationMapper.insert(reservation);
        } else {
            if (reservationMapper.selectById(request.getId()) == null) {
                throw new BusinessException("预约记录不存在");
            }
            reservation.setId(request.getId());
            reservationMapper.updateById(reservation);
        }
        return reservationMapper.selectById(reservation.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (reservationMapper.selectById(id) == null) {
            throw new BusinessException("预约记录不存在");
        }
        reservationMapper.deleteById(id);
    }
}
