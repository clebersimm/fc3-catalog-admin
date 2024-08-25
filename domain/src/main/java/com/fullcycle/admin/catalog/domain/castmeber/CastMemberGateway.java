package com.fullcycle.admin.catalog.domain.castmeber;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

import java.util.Optional;

public interface CastMemberGateway {
    CastMember create(CastMember aCastMember);
    void deleteById(CastMemberId anId);
    Optional<CastMember> findById(CastMemberId anID);
    CastMember update(CastMember genre);
    Pagination<CastMember> findAll(SearchQuery aQuery);
}
