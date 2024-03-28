package com.fullcycle.admin.catalog.infrastructure.category.models;

import java.io.IOException;
import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.fullcycle.admin.catalog.JacksonTest;

@JacksonTest
public class UpdateCategoryApiInputTest {
    @Autowired
    private JacksonTester<UpdateCategoryApiInput> json;

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedName = "Filmes";
        final var expectedDescription = "Mais assistida";
        final var expectedIsActive = false;

        final var json = """
                {
                    "name":"%s",
                    "description":"%s",
                    "is_active": %s
                }
                """.formatted(expectedName, expectedDescription, expectedIsActive);

        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("description", expectedDescription)
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }
}
