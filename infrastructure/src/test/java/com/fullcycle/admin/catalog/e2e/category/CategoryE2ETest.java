package com.fullcycle.admin.catalog.e2e.category;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fullcycle.admin.catalog.E2ETest;
import com.fullcycle.admin.catalog.application.category.retrive.get.CategoryOutput;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infrastructure.configuration.json.Json;

@E2ETest
@Testcontainers
public class CategoryE2ETest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CategoryRepository categoryRepository;
    @Container
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer("mysql:latest")
            .withPassword("123456")
            .withUsername("root")
            .withDatabaseName("adm_videos");

    @DynamicPropertySource
    public static void setDataSourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("mysql.port", () -> MY_SQL_CONTAINER.getMappedPort(3306));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToCreateANewCategoryWithValidValues() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var actualId = givenACategory(expectedName, expectedDescription, expectedIsActive);
        final var actualCategory = retriveACategory(actualId.getValue());
        Assertions.assertEquals(expectedName, actualCategory.name());
        Assertions.assertEquals(expectedDescription, actualCategory.description());
        Assertions.assertEquals(expectedIsActive, actualCategory.active());
        Assertions.assertNotNull(actualCategory.createdAt());
        Assertions.assertNotNull(actualCategory.updatedAt());
        Assertions.assertNull(actualCategory.deletedAt());
    }

    private CategoryApiOutput retriveACategory(final String anID) {
        final var aRequest = MockMvcRequestBuilders.get("/categories/" + anID).contentType(MediaType.APPLICATION_JSON);
        final var json = this.mvc.perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();
        return Json.readValue(json, CategoryApiOutput.class);
    }

    private CategoryID givenACategory(final String expectedName,final String expectedDescription,final boolean expectedIsActive) {
        final var request = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
        final var aRequest = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON).content(Json.writeValueAsString(request));
        final var actualId = this.mvc.perform(aRequest)
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn()
            .getResponse().getHeader("Location")
            .replace("/categories/", "");
        return CategoryID.from(actualId);

    
}
