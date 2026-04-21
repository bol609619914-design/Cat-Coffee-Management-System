package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catcoffee.backend.entity.Cat;
import com.catcoffee.backend.entity.CustomerOrder;
import com.catcoffee.backend.entity.Drink;
import com.catcoffee.backend.entity.Reservation;
import com.catcoffee.backend.mapper.CatMapper;
import com.catcoffee.backend.mapper.CustomerOrderMapper;
import com.catcoffee.backend.mapper.DrinkMapper;
import com.catcoffee.backend.mapper.ReservationMapper;
import com.catcoffee.backend.service.DashboardService;
import com.catcoffee.backend.vo.DashboardVO;
import com.catcoffee.backend.vo.NameValueVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CatMapper catMapper;
    private final ReservationMapper reservationMapper;
    private final CustomerOrderMapper customerOrderMapper;
    private final DrinkMapper drinkMapper;

    @Override
    public DashboardVO dashboard() {
        long catCount = catMapper.selectCount(new LambdaQueryWrapper<Cat>());
        long reservationCount = reservationMapper.selectCount(new LambdaQueryWrapper<Reservation>());
        long activeOrderCount = customerOrderMapper.selectCount(new LambdaQueryWrapper<CustomerOrder>()
                .in(CustomerOrder::getOrderStatus, List.of("待制作", "已完成")));

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        List<CustomerOrder> orders = customerOrderMapper.selectList(new LambdaQueryWrapper<CustomerOrder>()
                .ge(CustomerOrder::getCreateTime, todayStart));
        BigDecimal todayRevenue = orders.stream()
                .map(CustomerOrder::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<NameValueVO> hotDrinks = drinkMapper.selectList(new LambdaQueryWrapper<Drink>())
                .stream()
                .sorted(Comparator.comparing(Drink::getSales, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(item -> new NameValueVO(item.getName(), item.getSales() == null ? 0L : item.getSales().longValue()))
                .toList();

        Map<String, Long> reservationStatus = reservationMapper.selectList(new LambdaQueryWrapper<Reservation>())
                .stream()
                .collect(Collectors.groupingBy(Reservation::getStatus, Collectors.counting()));
        List<NameValueVO> reservationStatusSummary = reservationStatus.entrySet().stream()
                .map(entry -> new NameValueVO(entry.getKey(), entry.getValue()))
                .toList();

        return new DashboardVO(catCount, reservationCount, activeOrderCount, todayRevenue, hotDrinks, reservationStatusSummary);
    }
}
