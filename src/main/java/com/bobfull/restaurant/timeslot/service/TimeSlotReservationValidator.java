package com.bobfull.restaurant.timeslot.service;

import com.bobfull.common.exception.CustomException;
import com.bobfull.common.exception.TimeSlotErrorCode;
import com.bobfull.restaurant.timeslot.port.TimeSlotReservationUsagePort;
import org.springframework.stereotype.Service;

@Service
public class TimeSlotReservationValidator {

    private final TimeSlotReservationUsagePort reservationUsagePort;

    public TimeSlotReservationValidator(TimeSlotReservationUsagePort reservationUsagePort) {
        this.reservationUsagePort = reservationUsagePort;
    }

    public void validateChangeAllowed(Long sessionId) {
        if (reservationUsagePort.hasActiveReservation(sessionId)) {
            throw new CustomException(TimeSlotErrorCode.SESSION_HAS_RESERVATION);
        }
    }

    public void validateDeletionAllowed(Long sessionId) {
        validateChangeAllowed(sessionId);
    }
}
