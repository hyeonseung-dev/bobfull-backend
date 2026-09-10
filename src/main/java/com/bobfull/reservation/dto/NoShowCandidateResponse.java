package com.bobfull.reservation.dto;

import com.bobfull.common.support.MemberNameMasker;
import com.bobfull.reservation.entity.ParticipationStatus;
import com.bobfull.reservation.entity.ReservationParticipant;

public record NoShowCandidateResponse(
        Long participationId,
        Long memberId,
        String name,
        Integer partySize,
        ParticipationStatus participationStatus
) {
    public static NoShowCandidateResponse of(ReservationParticipant participant, String memberName) {
        return new NoShowCandidateResponse(
                participant.getId(), participant.getMemberId(), MemberNameMasker.mask(memberName),
                participant.getPartySize(), participant.getParticipationStatus());
    }
}
