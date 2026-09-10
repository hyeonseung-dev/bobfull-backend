package com.bobfull.sharedtable.service;

import com.bobfull.common.exception.CustomException;
import com.bobfull.common.exception.SharedTableErrorCode;
import com.bobfull.sharedtable.port.SharedTableUsagePort;
import org.springframework.stereotype.Service;

@Service
public class SharedTableUsageValidator {

    private final SharedTableUsagePort sharedTableUsagePort;

    public SharedTableUsageValidator(SharedTableUsagePort sharedTableUsagePort) {
        this.sharedTableUsagePort = sharedTableUsagePort;
    }

    public void validateCapacityChangeAllowed(Long tableId) {
        if (sharedTableUsagePort.hasActiveReservation(tableId)) {
            throw new CustomException(SharedTableErrorCode.TABLE_HAS_RESERVATION);
        }
    }

    public void validateDeletionAllowed(Long tableId) {
        if (sharedTableUsagePort.hasDiningSession(tableId)) {
            throw new CustomException(SharedTableErrorCode.TABLE_HAS_DINING_SESSION);
        }
    }
}
