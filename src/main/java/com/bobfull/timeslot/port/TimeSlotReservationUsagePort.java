package com.bobfull.timeslot.port;

public interface TimeSlotReservationUsagePort {

    boolean hasActiveReservation(Long timeSlotId);
}
