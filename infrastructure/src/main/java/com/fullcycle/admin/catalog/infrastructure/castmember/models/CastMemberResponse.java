package com.fullcycle.admin.catalog.infrastructure.castmember.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;

import java.time.Instant;

public record CastMemberResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") String type,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {

}
