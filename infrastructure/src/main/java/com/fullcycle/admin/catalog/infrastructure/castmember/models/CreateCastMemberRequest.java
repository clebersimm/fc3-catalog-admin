package com.fullcycle.admin.catalog.infrastructure.castmember.models;

import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;

public record CreateCastMemberRequest(String name, CastMemberType type) {
}
