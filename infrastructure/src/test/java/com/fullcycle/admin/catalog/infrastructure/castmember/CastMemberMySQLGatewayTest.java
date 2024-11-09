package com.fullcycle.admin.catalog.infrastructure.castmember;

import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
        final var expectedId = aMember.getId();
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

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertEquals(aMember.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

    @Test
    public void givenAValidCastMember_whenCallsUpdate_shoultRefreshIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;
        final var aMember = CastMember.newMember("vindi", CastMemberType.DIRECTOR);
        final var expectedId = aMember.getId();
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

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedMember.getName());
        Assertions.assertEquals(expectedType, persistedMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), persistedMember.getCreatedAt());
        Assertions.assertTrue(aMember.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }

    @Test
    public void givenAValidCastMember_whenCallsDeleteById_shouldDeleteIt() {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());
        // when
        castMemberGateway.deleteById(aMember.getId());
        // then
        Assertions.assertEquals(0, castMemberRepository.count());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());
        // when
        castMemberGateway.deleteById(CastMemberId.from("123"));
        // then
        Assertions.assertEquals(1, castMemberRepository.count());
    }

    @Test
    public void givenAValidCastMember_whenCallsFindById_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());
        // when
        final var actualMember = castMemberGateway.findById(expectedId).get();
        // then
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedId, actualMember.getId());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertEquals(aMember.getCreatedAt(), actualMember.getCreatedAt());
        Assertions.assertTrue(aMember.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));
    }

    @Test
    public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(aMember));
        Assertions.assertEquals(1, castMemberRepository.count());
        // when
        final var actualMember = castMemberGateway.findById(CastMemberId.from("123"));
        // then
        Assertions.assertTrue(actualMember.isEmpty());
    }

    @Test
    public void givenEmptyCastMembers_whenCallsFindAll_shouldReturnEmpty(){
        // given
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "vim,0,10,1,Vim Disel",
            "taran,0,10,1,Kent Tarantino",
            "jas,0,10,1,Jason Vor",
            "mar,0,10,1,Martin Scorsese",
            "kit,0,10,1,Martin Kit Ha",
    })
    public void givenAValidTerms_whenCallsFindAll_shouldReturnFiltered(final String expectedTerms,
                                                                       final int expectedPage,
                                                                       final int expectedPerPage,
                                                                       final int expectedItemsCount,
                                                                       final long expectedTotal,
                                                                       final String expectedName) {
        mockMembers();
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Jason Vor",
            "name,desc,0,10,5,5,Vim Disel",
            "createdAt,asc,0,10,5,5,Kit Ha",
            "createdAt,desc,0,10,5,5,Martin Scorsese",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnSorted(final String expectedSort,
                                                                                final String expectedDirection,
                                                                                final int expectedPage,
                                                                                final int expectedPerPage,
                                                                                final int expectedItemsCount,
                                                                                final long expectedTotal,
                                                                                final String expectedName) {
        mockMembers();
        final var expectedTerms = "";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedName, actualPage.items().get(0).getName());

    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Jason Vor;Kit Ha",
            "1,2,2,5,Martin Scorsese;Kent Tarantino",
            "2,2,1,5,Vim Disel",
    })
    public void givenAValidPagination_whenCallsFindAll_shouldReturnPaginated(final int expectedPage,
                                                                             final int expectedPerPage,
                                                                             final int expectedItemsCount,
                                                                             final long expectedTotal,
                                                                             final String expectedNames){
        mockMembers();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        // when
        final var actualPage = castMemberGateway.findAll(aQuery);

        // then
        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedName: expectedNames.split(";")){
            Assertions.assertEquals(expectedName, actualPage.items().get(index).getName());
            index++;
        }
    }

    private void mockMembers() {
        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(CastMember.newMember("Kit Ha", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Vim Disel", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Kent Tarantino", CastMemberType.DIRECTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Jason Vor", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Martin Scorsese", CastMemberType.DIRECTOR))
        ));
    }
}