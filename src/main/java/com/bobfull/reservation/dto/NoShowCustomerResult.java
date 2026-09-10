package com.bobfull.reservation.dto;

import java.time.Instant;

public record NoShowCustomerResult(
        Long memberId,
        String memberName,
        long noShowCount,
        Instant latestNoShowAt,
        Long reservationId,
        Long participationId,
        Integer partySize
) {
}
