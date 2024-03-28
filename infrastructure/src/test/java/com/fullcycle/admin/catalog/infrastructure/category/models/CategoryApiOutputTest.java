package com.fullcycle.admin.catalog.infrastructure.category.models;

import java.io.IOException;
import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.fullcycle.admin.catalog.JacksonTest;

@JacksonTest
public class CategoryApiOutputTest {
    @Autowired
    private JacksonTester<CategoryApiOutput> json;

    @Test
    public void testMarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "Mais assistida";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var response = new CategoryApiOutput(expectedId, expectedName, expectedDescription, expectedIsActive,
                expectedCreatedAt,
                expectedUpdatedAt, expectedDeletedAt);
        final var actualJson = this.json.write(response);
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.is_active", expectedIsActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString())
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt.toString());
    }

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "Mais assistida";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();
        final var expectedUpdatedAt = Instant.now();
        final var json = """
                {
                    "id":"%s",
                    "name":"%s",
                    "description":"%s",
                    "is_active": %s,
                    "created_at": "%s",
                    "deleted_at": "%s",
                    "updated_at": "%s"
                }
                """.formatted(expectedId, expectedName, expectedDescription, expectedIsActive, expectedCreatedAt,
                expectedDeletedAt, expectedUpdatedAt);

        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("active", expectedIsActive)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt);
    }
}
