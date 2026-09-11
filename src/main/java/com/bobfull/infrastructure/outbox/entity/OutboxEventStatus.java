package com.bobfull.infrastructure.outbox.entity;

public enum OutboxEventStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
