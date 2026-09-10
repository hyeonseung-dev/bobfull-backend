package com.bobfull.admin.dto;

import com.bobfull.restaurant.entity.RestaurantStatus;
import java.time.Instant;

public record AdminRestaurantResult(
        Long restaurantId,
        Long ownerMemberId,
        String ownerName,
        String name,
        String address,
        String category,
        String description,
        String keyword,
        Integer depositPerPerson,
        RestaurantStatus status,
        Instant createdAt,
        Instant deletedAt
) {
}
