package com.bobfull.sharedtable.port;

import java.util.Collection;

public interface SharedTableReservationUsagePort {

    boolean hasActiveReservation(Collection<Long> timeSlotIds);
}
