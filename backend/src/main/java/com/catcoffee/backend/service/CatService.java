package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.CatSaveRequest;
import com.catcoffee.backend.entity.Cat;

public interface CatService {

    PageResult<Cat> list(long current, long size, String keyword);

    Cat save(CatSaveRequest request);

    void delete(Long id);
}
