package com.bobfull.reservation.dto;

import com.bobfull.common.support.MemberNameMasker;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public record NoShowCustomerResponse(
        Long memberId,
        String name,
        long noShowCount,
        OffsetDateTime latestNoShowAt,
        Long reservationId,
        Long participationId,
        Integer partySize
) {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    public static NoShowCustomerResponse of(NoShowCustomerResult result) {
        return new NoShowCustomerResponse(
                result.memberId(),
                MemberNameMasker.mask(result.memberName()),
                result.noShowCount(),
                OffsetDateTime.ofInstant(result.latestNoShowAt(), SEOUL),
                result.reservationId(),
                result.participationId(),
                result.partySize());
    }
}
