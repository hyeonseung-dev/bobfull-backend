package com.bobfull.reservation.dto;

import com.bobfull.reservation.entity.RecruitmentStatus;
import com.bobfull.reservation.entity.ReservationStatus;
import java.time.OffsetDateTime;

public record OwnerReservationDetailResponse(
        Long reservationId,
        Long restaurantId,
        Long sessionId,
        Long tableId,
        Integer capacity,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        ReservationStatus reservationStatus,
        RecruitmentStatus recruitmentStatus,
        Integer currentParticipantCount,
        Integer availableCapacity,
        Integer confirmationThreshold
) {
}
