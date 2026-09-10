package com.bobfull.admin.dto;

public record AdminMemberNoShowRateResult(
        Long memberId,
        String name,
        long totalReservationCount,
        long noShowCount
) {
}
