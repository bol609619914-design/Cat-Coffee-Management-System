package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CatSaveRequest;
import com.catcoffee.backend.entity.Cat;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.CatMapper;
import com.catcoffee.backend.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CatServiceImpl implements CatService {

    private final CatMapper catMapper;

    @Override
    public PageResult<Cat> list(long current, long size, String keyword) {
        LambdaQueryWrapper<Cat> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(keyword), Cat::getName, keyword)
                .or(StringUtils.hasText(keyword))
                .like(StringUtils.hasText(keyword), Cat::getBreed, keyword)
                .orderByDesc(Cat::getCreateTime);
        Page<Cat> page = catMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Cat save(CatSaveRequest request) {
        Cat cat = new Cat();
        BeanUtils.copyProperties(request, cat);
        if (request.getId() == null) {
            catMapper.insert(cat);
        } else {
            validateExists(request.getId());
            cat.setId(request.getId());
            catMapper.updateById(cat);
        }
        return catMapper.selectById(cat.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        validateExists(id);
        catMapper.deleteById(id);
    }

    private void validateExists(Long id) {
        if (catMapper.selectById(id) == null) {
            throw new BusinessException("猫咪不存在");
        }
    }
}
