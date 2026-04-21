package com.catcoffee.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.TableSaveRequest;
import com.catcoffee.backend.entity.CafeTable;
import com.catcoffee.backend.exception.BusinessException;
import com.catcoffee.backend.mapper.CafeTableMapper;
import com.catcoffee.backend.service.CafeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class CafeTableServiceImpl implements CafeTableService {

    private final CafeTableMapper cafeTableMapper;

    @Override
    public PageResult<CafeTable> list(long current, long size, String status) {
        LambdaQueryWrapper<CafeTable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), CafeTable::getStatus, status)
                .orderByAsc(CafeTable::getTableNo);
        Page<CafeTable> page = cafeTableMapper.selectPage(new Page<>(current, size), wrapper);
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CafeTable save(TableSaveRequest request) {
        CafeTable cafeTable = new CafeTable();
        BeanUtils.copyProperties(request, cafeTable);
        if (request.getId() == null) {
            cafeTableMapper.insert(cafeTable);
        } else {
            if (cafeTableMapper.selectById(request.getId()) == null) {
                throw new BusinessException("桌台不存在");
            }
            cafeTable.setId(request.getId());
            cafeTableMapper.updateById(cafeTable);
        }
        return cafeTableMapper.selectById(cafeTable.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (cafeTableMapper.selectById(id) == null) {
            throw new BusinessException("桌台不存在");
        }
        cafeTableMapper.deleteById(id);
    }
}
