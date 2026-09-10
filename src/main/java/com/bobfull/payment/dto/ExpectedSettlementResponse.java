package com.bobfull.payment.dto;

import java.math.BigDecimal;

public record ExpectedSettlementResponse(
        BigDecimal totalPaidAmount,
        BigDecimal totalRefundedAmount,
        BigDecimal expectedSettlementAmount
) {
}
