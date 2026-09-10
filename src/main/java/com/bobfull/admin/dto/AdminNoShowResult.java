package com.bobfull.admin.dto;

import java.time.Instant;

public record AdminNoShowResult(
        Long noShowHistoryId,
        Long memberId,
        String memberName,
        Long restaurantId,
        String restaurantName,
        Long reservationId,
        Long participationId,
        Integer partySize,
        Instant processedAt
) {
}
