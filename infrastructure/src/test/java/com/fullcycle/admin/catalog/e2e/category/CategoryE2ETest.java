package com.fullcycle.admin.catalog.e2e.category;

import javax.net.ssl.SSLEngineResult.Status;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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

    @Test
    public void asACatalogAdminIShouldBeAbleToNavigateToAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", "", true);
        givenACategory("Documentarios", "", true);
        givenACategory("Series", "", true);
        listCategories(0, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo("Documentarios")));
        listCategories(1, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo("Filmes")));
        listCategories(2, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo("Series")));
        listCategories(3, 1)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(0)));
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSearchBetweenAllCategories() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", "", true);
        givenACategory("Documentarios", "", true);
        givenACategory("Series", "", true);
        listCategories(0, 1, "filme")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo("Filmes")));

    }

    @Test
    public void asACatalogAdminIShouldBeAbleToSortAllCategoriesByDescriptionDesc() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        givenACategory("Filmes", "C", true);
        givenACategory("Documentarios", "Z", true);
        givenACategory("Series", "A", true);
        listCategories(0, 3, "", "description", "desc")
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.equalTo(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo("Documentarios")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[1].name", Matchers.equalTo("Filmes")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[2].name", Matchers.equalTo("Series")));

    }

    private ResultActions listCategories(final int page, final int perPage, final String search) throws Exception {
        return listCategories(page, perPage, search, "", "");
    }

    private ResultActions listCategories(final int page, final int perPage) throws Exception {
        return listCategories(page, perPage, "", "", "");
    }

    private ResultActions listCategories(final int page, final int perPage, final String search, final String sort,
            final String directions) throws Exception {
        final var aRequest = MockMvcRequestBuilders.get("/categories/")
                .queryParam("page", String.valueOf(page))
                .queryParam("perPage", String.valueOf(perPage))
                .queryParam("search", search)
                .queryParam("sort", sort)
                .queryParam("dir", directions)
                .contentType(MediaType.APPLICATION_JSON);
        return this.mvc.perform(aRequest);
    }

    private CategoryApiOutput retriveACategory(final String anID) throws Exception {
        final var aRequest = MockMvcRequestBuilders.get("/categories/" + anID).contentType(MediaType.APPLICATION_JSON);
        final var json = this.mvc.perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getContentAsString();
        return Json.readValue(json, CategoryApiOutput.class);
    }

    private CategoryID givenACategory(final String expectedName, final String expectedDescription,
            final boolean expectedIsActive) throws Exception {
        final var request = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
        final var aRequest = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                .content(Json.writeValueAsString(request));
        final var actualId = this.mvc.perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn()
                .getResponse().getHeader("Location")
                .replace("/categories/", "");
        return CategoryID.from(actualId);
    }

    @Test
    public void asACatalogAdminIShouldBeAbleToGetACategoryById() throws Exception {
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

    @Test
    public void asACatalogAdminIShouldBeAbleToGetANotFoundErrorWhenGettingCateogy() throws Exception {
        Assertions.assertTrue(MY_SQL_CONTAINER.isRunning());
        Assertions.assertEquals(0, categoryRepository.count());
        final var aRequest = MockMvcRequestBuilders.post("/categories/123")
                .contentType(MediaType.APPLICATION_JSON);
        this.mvc.perform(aRequest)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",Matchers.equalTo("Category with ID 123 was not found")));
    }

}
