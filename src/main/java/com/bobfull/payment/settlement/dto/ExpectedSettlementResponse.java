package com.bobfull.payment.settlement.dto;

import java.math.BigDecimal;

public record ExpectedSettlementResponse(
        BigDecimal totalPaidAmount,
        BigDecimal totalRefundedAmount,
        BigDecimal expectedSettlementAmount
) {
}
