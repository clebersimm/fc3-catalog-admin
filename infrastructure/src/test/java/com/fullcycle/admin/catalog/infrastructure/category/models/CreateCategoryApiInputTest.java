package com.fullcycle.admin.catalog.infrastructure.category.models;

import java.io.IOException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import com.fullcycle.admin.catalog.JacksonTest;

@JacksonTest
public class CreateCategoryApiInputTest {
    @Autowired
    private JacksonTester<CreateCategoryApiInput> json;

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

    @Test
    public void testMarshall() throws IOException {
        final var expectedName = "Filmes";
        final var expectedDescription = "Mais assistida";
        final var expectedIsActive = false;

        final var input = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        final var actualJson = this.json.write(input);
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.description", expectedDescription)
                .hasJsonPathValue("$.is_active", expectedIsActive);
    }    
}
