package com.bobfull.admin.dto;

import com.bobfull.reservation.entity.RecruitmentStatus;
import com.bobfull.reservation.entity.ReservationStatus;
import java.time.Instant;

public record AdminReservationResult(
        Long reservationId,
        Long restaurantId,
        String restaurantName,
        Long creatorMemberId,
        Instant startAt,
        ReservationStatus reservationStatus,
        RecruitmentStatus recruitmentStatus,
        long currentParticipantCount,
        Integer capacity
) {
}
