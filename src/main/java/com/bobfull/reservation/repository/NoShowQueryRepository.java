package com.bobfull.reservation.repository;

import com.bobfull.reservation.dto.NoShowCustomerResult;
import com.bobfull.reservation.dto.NoShowHistoryResult;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoShowQueryRepository {

    Page<NoShowHistoryResult> findHistoriesByReservationId(Long reservationId, Pageable pageable);

    Page<NoShowCustomerResult> findNoShowCustomers(Long restaurantId, Instant startAt, Instant endAt, Pageable pageable);
}
