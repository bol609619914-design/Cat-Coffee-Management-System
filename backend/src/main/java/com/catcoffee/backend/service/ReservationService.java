package com.catcoffee.backend.service;

import com.catcoffee.backend.common.PageResult;
import com.catcoffee.backend.dto.ReservationCreateRequest;
import com.catcoffee.backend.entity.Reservation;

public interface ReservationService {

    PageResult<Reservation> list(long current, long size, String status);

    Reservation save(ReservationCreateRequest request);

    void delete(Long id);
}
