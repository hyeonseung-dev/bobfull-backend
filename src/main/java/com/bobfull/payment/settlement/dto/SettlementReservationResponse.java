package com.bobfull.payment.settlement.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SettlementReservationResponse(
        Long reservationId,
        OffsetDateTime diningSessionAt,
        BigDecimal totalPaidAmount,
        BigDecimal totalRefundedAmount,
        BigDecimal expectedSettlementAmount
) {
}
