package com.bobfull.reservation.dto;

import com.bobfull.reservation.entity.RecruitmentStatus;
import com.bobfull.reservation.entity.ReservationStatus;
import java.time.OffsetDateTime;

public record OwnerReservationListItemResponse(
        Long reservationId,
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
    public static OwnerReservationListItemResponse of(
            OwnerReservationResult result,
            OffsetDateTime startAt,
            OffsetDateTime endAt
    ) {
        return new OwnerReservationListItemResponse(
                result.reservationId(),
                result.sessionId(),
                result.tableId(),
                result.capacity(),
                startAt,
                endAt,
                result.reservationStatus(),
                result.recruitmentStatus(),
                Math.toIntExact(result.currentParticipantCount()),
                result.availableCapacity(),
                result.confirmationThreshold()
        );
    }
}
