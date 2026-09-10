package com.bobfull.reservation.dto;

import java.time.Instant;

public record NoShowHistoryResult(
        Long noShowHistoryId,
        Long participationId,
        Long memberId,
        String memberName,
        Integer partySize,
        boolean marked,
        Long processedByMemberId,
        Instant processedAt
) {
}
