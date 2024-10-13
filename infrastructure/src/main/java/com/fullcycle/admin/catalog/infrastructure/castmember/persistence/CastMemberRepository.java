package com.fullcycle.admin.catalog.infrastructure.castmember.persistence;

public interface CastMemberRepository extends JpaRepository<CastMemberJpaEntity, String> {
    Page<CastMemberJpaEntity> findAll(Specification<CastMemberJpaEntity> spacefication, Pageable page);

}
