package com.bobfull.payment.dto;

import java.math.BigDecimal;
import java.util.List;

public record SettlementReservationDetailResponse(
        Long reservationId,
        BigDecimal expectedSettlementAmount,
        List<PaymentItem> payments,
        List<RefundItem> refunds
) {
    public record PaymentItem(String paymentId, String paymentStatus, BigDecimal amount) {
    }

    public record RefundItem(Long refundId, String refundStatus, BigDecimal amount) {
    }
}
