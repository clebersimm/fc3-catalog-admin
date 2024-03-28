package com.fullcycle.admin.catalog.infrastructure.category.models;

import com.fullcycle.admin.catalog.JacksonTest;

import java.io.IOException;
import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

@JacksonTest
public class CategoryListResponseTest {
    @Autowired
    private JacksonTester<CategoryListResponse> json;

    @Test
    public void testMarshall() throws IOException{
        final var expectedId = "123";
        final var expectedName = "Filmes";
        final var expectedDescription = "Mais assistida";
        final var expectedIsActive = false;
        final var expectedCreatedAt = Instant.now();
        final var expectedDeletedAt = Instant.now();
        final var response = new CategoryListResponse(expectedId, expectedName, expectedDescription, expectedIsActive,
                expectedCreatedAt,
                expectedDeletedAt);
        final var actualJson = this.json.write(response);
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.id", expectedId)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.is_active", expectedIsActive)
                .hasJsonPathValue("$.created_at", expectedCreatedAt.toString())
                .hasJsonPathValue("$.deleted_at", expectedDeletedAt.toString());
    }
}
