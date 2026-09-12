package com.bobfull.restaurant.timeslot.service;

import com.bobfull.common.exception.CommonErrorCode;
import com.bobfull.common.exception.CustomException;
import com.bobfull.common.exception.RestaurantErrorCode;
import com.bobfull.payment.service.PaymentHoldReader;
import com.bobfull.reservation.entity.ParticipationStatus;
import com.bobfull.reservation.entity.Reservation;
import com.bobfull.reservation.entity.ReservationStatus;
import com.bobfull.reservation.policy.ReservationCapacityPolicy;
import com.bobfull.reservation.repository.ReservationParticipantRepository;
import com.bobfull.reservation.repository.ReservationRepository;
import com.bobfull.restaurant.entity.Restaurant;
import com.bobfull.restaurant.repository.RestaurantRepository;
import com.bobfull.restaurant.sharedtable.entity.SharedTable;
import com.bobfull.restaurant.sharedtable.repository.SharedTableRepository;
import com.bobfull.restaurant.timeslot.dto.AvailableDiningSessionListResponse;
import com.bobfull.restaurant.timeslot.dto.AvailableDiningSessionResponse;
import com.bobfull.restaurant.timeslot.entity.TimeSlot;
import com.bobfull.restaurant.timeslot.repository.TimeSlotRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvailableDiningSessionQueryService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final List<ReservationStatus> ACTIVE_RESERVATION_STATUSES =
            List.of(ReservationStatus.RECRUITING, ReservationStatus.CONFIRMED, ReservationStatus.CANCELLING);
    private static final List<ReservationStatus> CLOSED_RESERVATION_STATUS = List.of(ReservationStatus.CLOSED);
    private static final List<ParticipationStatus> OCCUPYING_PARTICIPATION_STATUSES =
            List.of(ParticipationStatus.RESERVED, ParticipationStatus.CANCEL_REQUESTED);

    private final TimeSlotRepository timeSlotRepository;
    private final SharedTableRepository sharedTableRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationParticipantRepository reservationParticipantRepository;
    private final PaymentHoldReader paymentHoldReader;

    public AvailableDiningSessionQueryService(
            TimeSlotRepository timeSlotRepository,
            SharedTableRepository sharedTableRepository,
            RestaurantRepository restaurantRepository,
            ReservationRepository reservationRepository,
            ReservationParticipantRepository reservationParticipantRepository,
            PaymentHoldReader paymentHoldReader
    ) {
        this.timeSlotRepository = timeSlotRepository;
        this.sharedTableRepository = sharedTableRepository;
        this.restaurantRepository = restaurantRepository;
        this.reservationRepository = reservationRepository;
        this.reservationParticipantRepository = reservationParticipantRepository;
        this.paymentHoldReader = paymentHoldReader;
    }

    @Transactional(readOnly = true)
    public AvailableDiningSessionListResponse getAvailableDiningSessions(
            Long restaurantId,
            LocalDate date,
            Integer partySize
    ) {
        validatePartySize(partySize);
        findActiveRestaurantOrThrow(restaurantId);

        List<SharedTable> sharedTables = sharedTableRepository.findAllByRestaurantIdAndDeletedAtIsNull(restaurantId);
        if (sharedTables.isEmpty()) {
            return new AvailableDiningSessionListResponse(restaurantId, List.of());
        }

        Map<Long, Integer> capacityByTableId = capacityByTableId(sharedTables);
        DateRange dateRange = toDateRange(date);
        List<TimeSlot> timeSlots = timeSlotRepository
                .findAllBySharedTableIdInAndStartAtGreaterThanEqualAndStartAtLessThanAndDeletedAtIsNullOrderByStartAtAsc(
                        capacityByTableId.keySet(), dateRange.startAt(), dateRange.endAt());
        if (timeSlots.isEmpty()) {
            return new AvailableDiningSessionListResponse(restaurantId, List.of());
        }

        AvailableDiningSessionBatchContext context = loadAvailableDiningSessionBatchContext(timeSlots);
        List<AvailableDiningSessionResponse> content = timeSlots.stream()
                .map(timeSlot -> toAvailableDiningSessionResponse(
                        timeSlot,
                        capacityByTableId.get(timeSlot.getSharedTableId()),
                        context
                ))
                .filter(response -> partySize == null || response.availableCapacity() >= partySize)
                .toList();
        return new AvailableDiningSessionListResponse(restaurantId, content);
    }

    private AvailableDiningSessionBatchContext loadAvailableDiningSessionBatchContext(List<TimeSlot> timeSlots) {
        List<Long> timeSlotIds = timeSlots.stream().map(TimeSlot::getId).toList();

        Map<Long, Reservation> activeReservationByTimeSlotId = reservationRepository
                .findAllByTimeSlotIdInAndReservationStatusIn(timeSlotIds, ACTIVE_RESERVATION_STATUSES)
                .stream()
                .collect(Collectors.toMap(Reservation::getTimeSlotId, reservation -> reservation));
        Set<Long> closedTimeSlotIds = reservationRepository
                .findAllByTimeSlotIdInAndReservationStatusIn(timeSlotIds, CLOSED_RESERVATION_STATUS)
                .stream()
                .map(Reservation::getTimeSlotId)
                .collect(Collectors.toSet());

        List<Long> activeReservationIds = activeReservationByTimeSlotId.values().stream()
                .map(Reservation::getId)
                .toList();
        Map<Long, Integer> participantCountByReservationId = activeReservationIds.isEmpty()
                ? Map.of()
                : reservationParticipantRepository
                        .sumPartySizeByReservationIdsAndStatuses(activeReservationIds, OCCUPYING_PARTICIPATION_STATUSES)
                        .stream()
                        .collect(Collectors.toMap(row -> (Long) row[0], row -> ((Number) row[1]).intValue()));
        Map<Long, Integer> readyHoldPartySizeByTimeSlotId = paymentHoldReader
                .sumActiveReadyPartySizeByTimeSlotIds(timeSlotIds);

        return new AvailableDiningSessionBatchContext(
                activeReservationByTimeSlotId, closedTimeSlotIds, participantCountByReservationId, readyHoldPartySizeByTimeSlotId);
    }

    private AvailableDiningSessionResponse toAvailableDiningSessionResponse(
            TimeSlot timeSlot,
            Integer capacity,
            AvailableDiningSessionBatchContext context
    ) {
        Reservation activeReservation = context.activeReservationByTimeSlotId().get(timeSlot.getId());
        Long reservationId = activeReservation != null ? activeReservation.getId() : null;
        int currentParticipantCount = reservationId != null
                ? context.participantCountByReservationId().getOrDefault(reservationId, 0)
                : 0;
        int availableCapacity = context.closedTimeSlotIds().contains(timeSlot.getId())
                ? 0
                : ReservationCapacityPolicy.availableCapacity(
                        capacity,
                        currentParticipantCount,
                        context.readyHoldPartySizeByTimeSlotId().getOrDefault(timeSlot.getId(), 0));
        return AvailableDiningSessionResponse.of(
                timeSlot,
                capacity,
                toOffsetDateTime(timeSlot.getStartAt()),
                toOffsetDateTime(timeSlot.getEndAt()),
                availableCapacity,
                reservationId,
                currentParticipantCount
        );
    }

    private Map<Long, Integer> capacityByTableId(List<SharedTable> sharedTables) {
        return sharedTables.stream()
                .collect(Collectors.toMap(SharedTable::getId, SharedTable::getCapacity));
    }

    private Restaurant findActiveRestaurantOrThrow(Long restaurantId) {
        return restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new CustomException(RestaurantErrorCode.RESTAURANT_ID_NOT_FOUND));
    }

    private void validatePartySize(Integer partySize) {
        if (partySize != null && partySize <= 0) {
            throw new CustomException(CommonErrorCode.INVALID_INPUT_VALUE);
        }
    }

    private DateRange toDateRange(LocalDate date) {
        return new DateRange(toInstant(date, LocalTime.MIN), toInstant(date.plusDays(1), LocalTime.MIN));
    }

    private Instant toInstant(LocalDate date, LocalTime time) {
        return LocalDateTime.of(date, time).atZone(SEOUL_ZONE).toInstant();
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    private record AvailableDiningSessionBatchContext(
            Map<Long, Reservation> activeReservationByTimeSlotId,
            Set<Long> closedTimeSlotIds,
            Map<Long, Integer> participantCountByReservationId,
            Map<Long, Integer> readyHoldPartySizeByTimeSlotId
    ) {
    }

    private record DateRange(Instant startAt, Instant endAt) {
    }
}
