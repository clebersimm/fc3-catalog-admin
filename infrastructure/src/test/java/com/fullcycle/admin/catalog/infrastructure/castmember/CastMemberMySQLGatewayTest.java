package com.fullcycle.admin.catalog.infrastructure.castmember;

import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;

import static org.junit.jupiter.api.Assertions.*;

@MySQLGatewayTest
public class CastMemberMySQLGatewayTest {
    @Autowired
    private CastMemberMySQLGateway castMemberGateway;
    @Autowired
    private CastMemberRepository castMemberRepository;

    @Test
    public void testDependencies() {
        Assertions.assertNotNull(castMemberRepository);
        Assertions.assertNotNull(castMemberGateway);
    }

    @Test
    public void givenAValidCastMember_whenCallsCreate_shoultPersistIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expetedId = aMember.getId();
        Assertions.assertEquals(0, castMemberRepository.count());
        // when
        final var actualMember = castMemberGateway.create(CastMember.with(aMember));
        // then
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedId, actualMember.getId());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        Assertions.assertEquals(aMember.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = castMemberRepository.findById(expetedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertEquals(aMember.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

    @Test
    public void givenAValidCastMember_whenCallsUpdate_shoultRefreshIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = CastMember.ACTOR;
        final var aMember = CastMember.newMember("vindi", CastMemberType.DIRECTOR);
        final var expetedId = aMember.getId();
        final var currentMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals("vindi", currentMember.getName());
        Assertions.assertEquals(CastMemberType.DIRECTOR, currentMember.getType());
        // when
        final var actualMember = castMemberGateway.update(CastMember.with(aMember).update(expectedName, expectedType));
        // then
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedId, actualMember.getId());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        Assertions.assertTrue(aMember.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));

        final var persistedMember = castMemberRepository.findById(expetedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertTrue(aMember.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }
}