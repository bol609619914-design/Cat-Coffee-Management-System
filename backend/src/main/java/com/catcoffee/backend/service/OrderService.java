package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.OrderCreateRequest;
import com.catcoffee.backend.entity.CustomerOrder;

public interface OrderService {

    PageResult<CustomerOrder> list(long current, long size, String orderStatus, String payStatus);

    CustomerOrder save(OrderCreateRequest request);

    void delete(Long id);
}
