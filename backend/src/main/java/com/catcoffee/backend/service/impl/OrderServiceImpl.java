package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.OrderCreateRequest;
import com.catcoffee.backend.dto.OrderItemRequest;
import com.catcoffee.backend.entity.CafeTable;
import com.catcoffee.backend.entity.CustomerOrder;
import com.catcoffee.backend.entity.CustomerOrderItem;
import com.catcoffee.backend.entity.Drink;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.CafeTableMapper;
import com.catcoffee.backend.mapper.CustomerOrderItemMapper;
import com.catcoffee.backend.mapper.CustomerOrderMapper;
import com.catcoffee.backend.mapper.DrinkMapper;
import com.catcoffee.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CustomerOrderMapper customerOrderMapper;
    private final CustomerOrderItemMapper customerOrderItemMapper;
    private final DrinkMapper drinkMapper;
    private final CafeTableMapper cafeTableMapper;

    @Override
    public PageResult<CustomerOrder> list(long current, long size, String orderStatus, String payStatus) {
        LambdaQueryWrapper<CustomerOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(orderStatus), CustomerOrder::getOrderStatus, orderStatus)
                .eq(StringUtils.hasText(payStatus), CustomerOrder::getPayStatus, payStatus)
                .orderByDesc(CustomerOrder::getCreateTime);
        Page<CustomerOrder> page = customerOrderMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerOrder save(OrderCreateRequest request) {
        CafeTable cafeTable = cafeTableMapper.selectById(request.getTableId());
        if (cafeTable == null) {
            throw new BusinessException("桌台不存在");
        }

        CustomerOrder order = new CustomerOrder();
        BeanUtils.copyProperties(request, order);
        order.setOrderNo(buildOrderNo());
        order.setTotalAmount(calculateAndPersistItems(request.getItems(), order, request.getId()));

        if (request.getId() == null) {
            customerOrderMapper.insert(order);
            persistItems(request.getItems(), order);
        } else {
            CustomerOrder exist = customerOrderMapper.selectById(request.getId());
            if (exist == null) {
                throw new BusinessException("订单不存在");
            }
            order.setId(request.getId());
            order.setOrderNo(exist.getOrderNo());
            restoreInventory(request.getId());
            customerOrderMapper.updateById(order);
            customerOrderItemMapper.delete(new LambdaQueryWrapper<CustomerOrderItem>().eq(CustomerOrderItem::getOrderId, request.getId()));
            persistItems(request.getItems(), order);
        }
        return customerOrderMapper.selectById(order.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (customerOrderMapper.selectById(id) == null) {
            throw new BusinessException("订单不存在");
        }
        customerOrderMapper.deleteById(id);
        customerOrderItemMapper.delete(new LambdaQueryWrapper<CustomerOrderItem>().eq(CustomerOrderItem::getOrderId, id));
    }

    private BigDecimal calculateAndPersistItems(List<OrderItemRequest> items, CustomerOrder order, Long orderId) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            Drink drink = drinkMapper.selectById(item.getDrinkId());
            if (drink == null || drink.getStatus() == 0) {
                throw new BusinessException("存在不可售卖的饮品");
            }
            if (drink.getStock() < item.getQuantity()) {
                throw new BusinessException(drink.getName() + " 库存不足");
            }
            total = total.add(drink.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }

    private void persistItems(List<OrderItemRequest> items, CustomerOrder order) {
        if (order.getId() == null) {
            customerOrderMapper.insert(order);
        }
        for (OrderItemRequest item : items) {
            Drink drink = drinkMapper.selectById(item.getDrinkId());
            CustomerOrderItem orderItem = new CustomerOrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setDrinkId(drink.getId());
            orderItem.setDrinkName(drink.getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(drink.getPrice());
            orderItem.setSubtotal(drink.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            customerOrderItemMapper.insert(orderItem);

            drink.setStock(drink.getStock() - item.getQuantity());
            drink.setSales(drink.getSales() + item.getQuantity());
            drinkMapper.updateById(drink);
        }
    }

    private void restoreInventory(Long orderId) {
        List<CustomerOrderItem> oldItems = customerOrderItemMapper.selectList(
                new LambdaQueryWrapper<CustomerOrderItem>().eq(CustomerOrderItem::getOrderId, orderId)
        );
        for (CustomerOrderItem oldItem : oldItems) {
            Drink drink = drinkMapper.selectById(oldItem.getDrinkId());
            if (drink != null) {
                drink.setStock(drink.getStock() + oldItem.getQuantity());
                drink.setSales(Math.max(0, drink.getSales() - oldItem.getQuantity()));
                drinkMapper.updateById(drink);
            }
        }
    }

    private String buildOrderNo() {
        return "CC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}
