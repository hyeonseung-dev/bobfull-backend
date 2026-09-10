package com.bobfull.admin.dto;

public record AdminRestaurantStatisticsResult(
        Long restaurantId,
        String restaurantName,
        long totalReservationCount,
        long confirmedReservationCount
) {
}
