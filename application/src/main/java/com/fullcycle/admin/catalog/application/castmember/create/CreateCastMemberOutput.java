package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;

public record CreateCastMemberOutput(
        String id
) {
    public static CreateCastMemberOutput from(final CastMember aMember) {
        return new CreateCastMemberOutput(aMember.getId().getValue());
    }

    public static CreateCastMemberOutput from(final CastMemberId anId) {
        return new CreateCastMemberOutput(anId.getValue());
    }
}
