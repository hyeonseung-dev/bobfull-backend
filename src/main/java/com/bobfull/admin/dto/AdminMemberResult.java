package com.bobfull.admin.dto;

import com.bobfull.common.security.MemberRole;
import java.time.Instant;

public record AdminMemberResult(
        Long memberId,
        String email,
        String name,
        String phoneNumber,
        MemberRole role,
        long noShowCount,
        Instant createdAt,
        Instant deletedAt
) {
}
