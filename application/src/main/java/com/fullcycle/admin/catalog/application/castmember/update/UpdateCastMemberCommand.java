package com.fullcycle.admin.catalog.application.castmember.update;

import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;

public record UpdateCastMemberCommand(
        String id,
        String name,
        CastMemberType type
) {
    public static UpdateCastMemberCommand with(final String anId, final String aName, final CastMemberType type){
        return new UpdateCastMemberCommand(anId, aName, type);
    }
}
