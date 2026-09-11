package com.bobfull.restaurant.timeslot.dto;

public record DiningSessionBulkResponse(
        Long tableId,
        Integer createdSessionCount
) {
}
