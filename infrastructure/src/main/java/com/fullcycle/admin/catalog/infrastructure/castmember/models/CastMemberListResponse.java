package com.fullcycle.admin.catalog.infrastructure.castmember.models;

import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;

public record CastMemberListResponse(
        String id,
        String name,
        String type,
        String createdAt
) {
}
