package com.fullcycle.admin.catalog.infrastructure.castmember;

import com.fullcycle.admin.catalog.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CastMemberListResponse;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CastMemberResponse;

public interface CastMemberPresenter {
    static CastMemberResponse present(final CastMemberOutput aMember) {
        return new CastMemberResponse(
                aMember.id(),
                aMember.name(),
                aMember.type().name().toLowerCase(),
                aMember.createdAt().toString(),
                aMember.updatedAt().toString()
        );
    }
    static CastMemberListResponse present(final CastMemberListOutput aMember) {
        return new CastMemberListResponse(
                aMember.id(),
                aMember.name(),
                aMember.type().name().toLowerCase(),
                aMember.createdAt().toString()
        );
    }
}
