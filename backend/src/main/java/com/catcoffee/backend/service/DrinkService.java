package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.DrinkSaveRequest;
import com.catcoffee.backend.entity.Drink;

public interface DrinkService {

    PageResult<Drink> list(long current, long size, String keyword, Integer status);

    Drink save(DrinkSaveRequest request);

    void delete(Long id);
}
