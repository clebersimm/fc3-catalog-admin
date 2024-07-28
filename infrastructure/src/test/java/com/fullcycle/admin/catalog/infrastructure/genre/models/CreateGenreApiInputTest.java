package com.fullcycle.admin.catalog.infrastructure.genre.models;

import com.fullcycle.admin.catalog.JacksonTest;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

@JacksonTest
public class CreateGenreApiInputTest {
    @Autowired
    private JacksonTester<CreateGenreRequest> json;

    @Test
    public void testUnmarshall() throws IOException {
        final var expectedName = "Ação";
        final var expectedCategory = "123";
        final var expectedIsActive = false;

        final var json = """
                {
                    "name":"%s",
                    "categories_id":["%s"],
                    "is_active": %s
                }
                """.formatted(expectedName, expectedCategory, expectedIsActive);

        final var actualJson = this.json.parse(json);
        Assertions.assertThat(actualJson)
                .hasFieldOrPropertyWithValue("name", expectedName)
                .hasFieldOrPropertyWithValue("categories", List.of(expectedCategory))
                .hasFieldOrPropertyWithValue("active", expectedIsActive);
    }

    @Test
    public void testMarshall() throws IOException {
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = false;

        final var input = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);

        final var actualJson = this.json.write(input);
        Assertions.assertThat(actualJson)
                .hasJsonPathValue("$.name", expectedName)
                .hasJsonPathValue("$.categories_id", expectedCategories)
                .hasJsonPathValue("$.is_active", expectedIsActive);
    }    
}
