package com.bobfull.restaurant.sharedtable.dto;

import jakarta.validation.constraints.NotNull;

public record SharedTableRequest(
        @NotNull Integer capacity
) {
}
