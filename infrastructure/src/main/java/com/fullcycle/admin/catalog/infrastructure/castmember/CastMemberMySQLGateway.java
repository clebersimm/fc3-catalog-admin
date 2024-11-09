package com.fullcycle.admin.catalog.infrastructure.castmember;

import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;


import java.util.Objects;
import java.util.Optional;

public class CastMemberMySQLGateway implements CastMemberGateway {

    private final CastMemberRepository castMemberRepository;

    public CastMemberMySQLGateway(final CastMemberRepository castMemberRepository) {
        this.castMemberRepository = Objects.requireNonNull(castMemberRepository);
    }


    @Override
    public CastMember create(CastMember aCastMember) {
        return save(aCastMember);
    }

    @Override
    public void deleteById(CastMemberId aMemberId) {
        final var anId = aMemberId.getValue();
        if(this.castMemberRepository.existsById(anId)){
            this.castMemberRepository.deleteById(anId);
        }
    }

    @Override
    public Optional<CastMember> findById(CastMemberId anID) {
        return this.castMemberRepository.findById(anID.getValue()).map(CastMemberJpaEntity::toAggregate);
    }

    @Override
    public CastMember update(CastMember aCastMember) {
        return save(aCastMember);
    }

    @Override
    public Pagination<CastMember> findAll(SearchQuery aQuery) {

        final var page = PageRequest.of(
                aQuery.page(),
                aQuery.perPage(),
                Sort.by(Sort.Direction.fromString(aQuery.direction()), aQuery.sort())
        );
        final var where = Optional.ofNullable(aQuery.terms()).filter(str -> !str.isBlank()).map(this::assembleSpecification).orElse(null);
        final var pageResult = this.castMemberRepository.findAll(where,page);
        return new Pagination<>(
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.map(CastMemberJpaEntity::toAggregate).toList()
        );
    }

    private CastMember save(final CastMember aCastMember) {
        return this.castMemberRepository.save(CastMemberJpaEntity.from(aCastMember)).toAggregate();
    }

    private Specification<CastMemberJpaEntity> assembleSpecification(final String terms) {
        return SpecificationUtils.like("name", terms);
    }
}
