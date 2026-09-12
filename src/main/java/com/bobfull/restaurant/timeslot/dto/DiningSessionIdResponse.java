package com.bobfull.restaurant.timeslot.dto;

import com.bobfull.restaurant.timeslot.entity.TimeSlot;

public record DiningSessionIdResponse(Long sessionId) {

    public static DiningSessionIdResponse from(TimeSlot timeSlot) {
        return new DiningSessionIdResponse(timeSlot.getId());
    }
}
