package com.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fullcycle.admin.catalog.JacksonTest;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

@JacksonTest
public class GenreResponseTest {
    @Autowired
    private JacksonTester<GenreResponse> json;

    @Test
    public void testMarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123");
        final var expectedIsActive = false;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedDeletedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();
        final var response = new GenreResponse(expectedId, expectedName, expectedCategories,expectedIsActive,
                expectedCreatedAt,
                expectedUpdatedAt, expectedDeletedAt);
        final var actualJson = this.json.write(response);
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.categories_id", expectedCategories)
                .hasJsonPathValue("$.is_active", expectedIsActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString())
                .hasJsonPathValue("$.updated_at", expectedUpdatedAt.toString());
    }

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedId = "123";
        final var expectedName = "Ação";
        final var expectedCategory = "123";
        final var expectedIsActive = false;
        final var expectedCreatedAt = InstantUtils.now();
        final var expectedDeletedAt = InstantUtils.now();
        final var expectedUpdatedAt = InstantUtils.now();
        final var json = """
                {
                    "id":"%s",
                    "name":"%s",
                    "categories_id":["%s"],
                    "is_active": %s,
                    "created_at": "%s",
                    "deleted_at": "%s",
                    "updated_at": "%s"
                }
                """.formatted(expectedId, expectedName, expectedCategory, expectedIsActive, expectedCreatedAt,
                expectedDeletedAt, expectedUpdatedAt);

        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("id", expectedId)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategory))
                .hasFieldOrPropertyWithValue("active", expectedIsActive)
                .hasFieldOrPropertyWithValue("createdAt", expectedCreatedAt)
                .hasFieldOrPropertyWithValue("deletedAt", expectedDeletedAt)
                .hasFieldOrPropertyWithValue("updatedAt", expectedUpdatedAt);
    }
}
