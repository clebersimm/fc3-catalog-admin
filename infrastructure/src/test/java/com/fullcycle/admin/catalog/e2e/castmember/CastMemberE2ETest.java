package com.fullcycle.admin.catalog.e2e.castmember;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;
import com.fullcycle.admin.catalog.e2e.MockDsl;
import com.fullcycle.admin.catalog.infrastructure.castmember.persistence.CastMemberRepository;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@E2ETest
@Testcontainers
public class CastMemberE2ETest implements MockDsl {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @Container
    private static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123").withUsername("root").withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MYSQL_CONTAINER.getMappedPort(3306));
    }

    @Override
    public MockMvc mvc() {
        return this.mvc;
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateAnewCastMemberWithValidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var actualMemberId = givenACastMember(expectedName, expectedType);
        final var actualMember = castMemberRepository.findById(actualMemberId.getValue()).get();
        Assertions.assertEquals(expectedName, actualMember.getName());
        Assertions.assertEquals(expectedType, actualMember.getType());
        Assertions.assertNotNull(actualMember.getCreatedAt());
        Assertions.assertNotNull(actualMember.getUpdatedAt());
        Assertions.assertEquals(actualMember.getCreatedAt(),actualMember.getUpdatedAt());

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSeeTreatedErrorByCreateAnewCastMemberWithInvalidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "'name' should not be null";
        givenACastMemberResult(expectedName, expectedType)
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));

    }

    @Test
    public void asCatalogAdminIShouldBeAbleToNavigateThruAllMembers() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        givenACastMember("Actor", CastMemberType.ACTOR);
        givenACastMember("Director", CastMemberType.DIRECTOR);
        givenACastMember("the Actor", CastMemberType.ACTOR);

        listCastMembers(0,1).andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Actor")));

        listCastMembers(1,1).andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(1)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Director")));

        listCastMembers(2,1).andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(2)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("the Actor")));

        listCastMembers(3,1).andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(3)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(0)));
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToSearchThruAllMembers() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        givenACastMember("Actor", CastMemberType.ACTOR);
        givenACastMember("Director", CastMemberType.DIRECTOR);
        givenACastMember("the Actor", CastMemberType.ACTOR);

        listCastMembers(0,1,"dir").andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(1)))
                .andExpect(jsonPath("$.total", equalTo(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].name", equalTo("Director")));
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToSearchThruAllMembersByNameDesc() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        givenACastMember("Actor", CastMemberType.ACTOR);
        givenACastMember("Director", CastMemberType.DIRECTOR);
        givenACastMember("the Actor", CastMemberType.ACTOR);

        listCastMembers(0,3,"","name","desc").andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page", equalTo(0)))
                .andExpect(jsonPath("$.per_page", equalTo(3)))
                .andExpect(jsonPath("$.total", equalTo(3)))
                .andExpect(jsonPath("$.items", hasSize(3)))
                .andExpect(jsonPath("$.items[0].name", equalTo("the Actor")))
                .andExpect(jsonPath("$.items[1].name", equalTo("Director")))
                .andExpect(jsonPath("$.items[2].name", equalTo("Actor")));
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToGetACastMemberById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var actualId = givenACastMember(expectedName, expectedType);
        final var actualMember = retrieveACastMember(actualId);

        Assertions.assertEquals(expectedName, actualMember.name());
        Assertions.assertEquals(expectedType.name(), actualMember.type());
        Assertions.assertNotNull(actualMember.createdAt());
        Assertions.assertNotNull(actualMember.updatedAt());
        Assertions.assertEquals(actualMember.createdAt(),actualMember.updatedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeToSeeATreatedErrorByGettingAFotFoundCastMember() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        retrieveACastMemberResult(CastMemberId.from("123")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", equalTo("CastMember with ID 123 was not found")));
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToUpdateACastMemberById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedName = "The Actor";
        final var expectedType = CastMemberType.ACTOR;
        final var actualId = givenACastMember("Actor", CastMemberType.DIRECTOR);
        updateACastMember(actualId, expectedName, expectedType)
                .andExpect(status().isOk());
        final var actualMember = retrieveACastMember(actualId);
        Assertions.assertEquals(expectedName, actualMember.name());
        Assertions.assertEquals(expectedType.name(), actualMember.type());
        Assertions.assertNotNull(actualMember.createdAt());
        Assertions.assertNotNull(actualMember.updatedAt());
        Assertions.assertNotEquals(actualMember.createdAt(),actualMember.updatedAt());
    }

    @Test
    public void asCatalogAdminIShouldBeAbleToSeeATreatedErrorByUpdateACastMemberWithInvalidValues() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        final var expectedName = "";
        final var expectedType = CastMemberType.ACTOR;
        final var actualId = givenACastMember("Actor", CastMemberType.DIRECTOR);
        updateACastMember(actualId, expectedName, expectedType)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errors",hasSize(1)))
                .andExpect(jsonPath("$.errors[0].message",equalTo("'name' should not be empty")));

    }

    @Test
    public void asCatalogAdminIShouldBeAbleToDeleteACastMemberById() throws Exception {
        Assertions.assertTrue(MYSQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, castMemberRepository.count());
        givenACastMember(Fixture.name(), Fixture.CastMember.type());
        final var actualId = givenACastMember(Fixture.name(), Fixture.CastMember.type());
        Assertions.assertEquals(2, castMemberRepository.count());
        deleteCastMember(actualId)
                .andExpect(status().isNoContent());
        Assertions.assertEquals(1, castMemberRepository.count());
        Assertions.assertFalse(castMemberRepository.existsById(actualId.getValue()));
    }
}
