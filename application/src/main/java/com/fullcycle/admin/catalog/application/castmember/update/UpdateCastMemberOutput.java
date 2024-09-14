package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;

public record UpdateCastMemberOutput(
        String id
) {
    public static UpdateCastMemberOutput from(final CastMember aMember) {
        return new UpdateCastMemberOutput(aMember.getId().getValue());
    }
}
