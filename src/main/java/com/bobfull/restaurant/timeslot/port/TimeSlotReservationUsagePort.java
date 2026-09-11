package com.bobfull.restaurant.timeslot.port;

public interface TimeSlotReservationUsagePort {

    boolean hasActiveReservation(Long timeSlotId);
}
