package com.bobfull.restaurant.sharedtable.port;

public interface SharedTableUsagePort {

    boolean hasDiningSession(Long tableId);

    boolean hasActiveReservation(Long tableId);
}
