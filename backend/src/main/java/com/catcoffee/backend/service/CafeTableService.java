package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.TableSaveRequest;
import com.catcoffee.backend.entity.CafeTable;

public interface CafeTableService {

    PageResult<CafeTable> list(long current, long size, String status);

    CafeTable save(TableSaveRequest request);

    void delete(Long id);
}
