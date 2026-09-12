package com.bobfull.restaurant.sharedtable.adapter;

import com.bobfull.restaurant.sharedtable.port.SharedTableReservationUsagePort;
import com.bobfull.restaurant.sharedtable.port.SharedTableUsagePort;
import com.bobfull.restaurant.timeslot.entity.TimeSlot;
import com.bobfull.restaurant.timeslot.repository.TimeSlotRepository;
import org.springframework.stereotype.Component;

@Component
public class SharedTableUsageAdapter implements SharedTableUsagePort {

    private final TimeSlotRepository timeSlotRepository;
    private final SharedTableReservationUsagePort reservationUsagePort;

    public SharedTableUsageAdapter(
            TimeSlotRepository timeSlotRepository,
            SharedTableReservationUsagePort reservationUsagePort
    ) {
        this.timeSlotRepository = timeSlotRepository;
        this.reservationUsagePort = reservationUsagePort;
    }

    @Override
    public boolean hasDiningSession(Long tableId) {
        return timeSlotRepository.existsBySharedTableIdAndDeletedAtIsNull(tableId);
    }

    @Override
    public boolean hasActiveReservation(Long tableId) {
        return reservationUsagePort.hasActiveReservation(
                timeSlotRepository.findAllBySharedTableIdAndDeletedAtIsNull(tableId).stream()
                .map(TimeSlot::getId)
                .toList());
    }
}
