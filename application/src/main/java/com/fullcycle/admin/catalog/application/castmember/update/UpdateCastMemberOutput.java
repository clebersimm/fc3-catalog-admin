package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;

public record UpdateCastMemberOutput(
        String id
) {
    public static UpdateCastMemberOutput from(final CastMember aMember) {
        return new UpdateCastMemberOutput(aMember.getId().getValue());
    }

    public static UpdateCastMemberOutput from(final CastMemberId anId) {
        return new UpdateCastMemberOutput(anId.getValue());
    }
}
