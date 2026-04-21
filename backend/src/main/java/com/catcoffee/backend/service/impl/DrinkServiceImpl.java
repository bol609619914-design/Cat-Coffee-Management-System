package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.DrinkSaveRequest;
import com.catcoffee.backend.entity.Drink;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.DrinkMapper;
import com.catcoffee.backend.service.DrinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DrinkServiceImpl implements DrinkService {

    private final DrinkMapper drinkMapper;

    @Override
    public PageResult<Drink> list(long current, long size, String keyword, Integer status) {
        LambdaQueryWrapper<Drink> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Drink::getName, keyword)
                .eq(status != null, Drink::getStatus, status)
                .orderByDesc(Drink::getSales)
                .orderByDesc(Drink::getCreateTime);
        Page<Drink> page = drinkMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Drink save(DrinkSaveRequest request) {
        Drink drink = new Drink();
        BeanUtils.copyProperties(request, drink);
        if (request.getIsRecommended() == null) {
            drink.setIsRecommended(0);
        }
        if (request.getStatus() == null) {
            drink.setStatus(1);
        }
        if (request.getId() == null) {
            drink.setSales(0);
            drinkMapper.insert(drink);
        } else {
            Drink exist = drinkMapper.selectById(request.getId());
            if (exist == null) {
                throw new BusinessException("饮品不存在");
            }
            if (drink.getSales() == null) {
                drink.setSales(exist.getSales());
            }
            drink.setId(request.getId());
            drinkMapper.updateById(drink);
        }
        return drinkMapper.selectById(drink.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (drinkMapper.selectById(id) == null) {
            throw new BusinessException("饮品不存在");
        }
        drinkMapper.deleteById(id);
    }
}
