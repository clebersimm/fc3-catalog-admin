package com.fullcycle.admin.catalog.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.get.CategoryOutput;
import com.fullcycle.admin.catalog.application.category.retrive.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.list.CategoryListOutput;
import com.fullcycle.admin.catalog.application.category.retrive.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;

import io.vavr.API;

@ControllerTest(controllers = CategoryApi.class)
public class CategoryAPITest {
        @Autowired
        private MockMvc mvc;

        @Autowired
        private ObjectMapper mapper;

        @MockBean
        private CreateCategoryUseCase cretCreateCategoryUseCase;

        @MockBean
        private GetCategoryByIdUseCase getCategoryByIdUseCase;
        @MockBean
        private UpdateCategoryUseCase updateCategoryUseCase;
        @MockBean
        private DeleteCategoryUseCase deleteCategoryUseCase;
        @MockBean
        private ListCategoriesUseCase listCategoriesUseCase;

        @Test
        public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
                // given
                final var expectedName = "Filmes";
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;
                final var aInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
                // when
                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenReturn(API.Right(CreateCategoryOutput.from("123")));
                final var request = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aInput));

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpectAll(
                                MockMvcResultMatchers.status().isCreated(),
                                MockMvcResultMatchers.header().string("location", "/categories/123"),
                                MockMvcResultMatchers.header().string("Content-Type",
                                                MediaType.APPLICATION_JSON_VALUE),
                                MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo("123")));
                verify(cretCreateCategoryUseCase, times(1)).execute(argThat(
                                cmd -> Objects.equals(expectedName, cmd.name()) &&
                                                Objects.equals(expectedDescription, cmd.description())
                                                && Objects.equals(expectedIsActive, cmd.isActive())));
        }

        @Test
        public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
                // given
                final String expectedName = null;
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;
                final var expectedMessage = "'name' should not be null";
                final var aInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

                // when
                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenReturn(API.Left(Notification.create(new Error((expectedMessage)))));
                final var request = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aInput));

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpectAll(
                                MockMvcResultMatchers.status().isUnprocessableEntity(),
                                MockMvcResultMatchers.header().string("location", Matchers.nullValue()),
                                MockMvcResultMatchers.header().string("Content-Type",
                                                MediaType.APPLICATION_JSON_VALUE),
                                MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)),
                                MockMvcResultMatchers.jsonPath("$.errors[0].message",
                                                Matchers.equalTo(expectedMessage)));
                verify(cretCreateCategoryUseCase, times(1)).execute(argThat(
                                cmd -> Objects.equals(expectedName, cmd.name()) &&
                                                Objects.equals(expectedDescription, cmd.description())
                                                && Objects.equals(expectedIsActive, cmd.isActive())));
        }

        @Test
        public void givenAnInvalidCommand_whenCallsCreateCategory_thenShouldReturnDomainException() throws Exception {
                final String expectedName = null;
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;
                final var expectedMessage = "'name' should not be null";
                final var aInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenThrow(DomainException.with(new Error("'name' should not be null")));
                final var request = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aInput));

                this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print())
                                .andExpectAll(
                                                MockMvcResultMatchers.status().isUnprocessableEntity(),
                                                MockMvcResultMatchers.header().string("location", Matchers.nullValue()),
                                                MockMvcResultMatchers.header().string("Content-Type",
                                                                MediaType.APPLICATION_JSON_VALUE),
                                                MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)),
                                                MockMvcResultMatchers.jsonPath("$.errors[0].message",
                                                                Matchers.equalTo(expectedMessage)));
                verify(cretCreateCategoryUseCase, times(1)).execute(argThat(
                                cmd -> Objects.equals(expectedName, cmd.name()) &&
                                                Objects.equals(expectedDescription, cmd.description())
                                                && Objects.equals(expectedIsActive, cmd.isActive())));
        }

        @Test
        public void givenAValidId_whenCallsGetCategory_shouldReturnACategory() throws Exception {
                // given
                final var expectedName = "Filmes";
                final var expectedDescription = "A Categoria";
                final var expectedIsActive = true;
                final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
                final var expectedId = aCategory.getId();
                when(getCategoryByIdUseCase.execute(any())).thenReturn(CategoryOutput.from(aCategory));
                // when
                final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId.getValue())
                                .contentType(MediaType.APPLICATION_JSON);
                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id",
                                                Matchers.equalTo(expectedId.getValue())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Matchers.equalTo(expectedName)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.description",
                                                Matchers.equalTo(expectedDescription)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.is_active",
                                                Matchers.equalTo(expectedIsActive)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at",
                                                Matchers.equalTo(aCategory.getCreatedAt().toString())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at",
                                                Matchers.equalTo(aCategory.getUpdatedAt().toString())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_at",
                                                Matchers.equalTo(aCategory.getDeletedAt())));
                verify(getCategoryByIdUseCase, times(1)).execute(Mockito.eq(expectedId.getValue()));
        }

        @Test
        public void givenAnInvalidId_whenCallGetCategory_shouldRetunNotFound() throws Exception {
                // given
                final var expectedErrorMessage = "Category with ID 1234 was not found";
                final var expectedId = CategoryID.from("1234");
                Mockito.when(getCategoryByIdUseCase.execute(any()))
                                .thenThrow(NotFoundException.with(Category.class, expectedId));
                // when
                final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId.getValue())
                                .contentType(MediaType.APPLICATION_JSON);

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.equalTo(expectedErrorMessage)));
        }

        @Test
        public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() throws Exception {
                // given
                final var expectedId = "123";
                final var expectedName = "Filmes";
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;
                when(updateCategoryUseCase.execute(any())).thenReturn(API.Right(UpdateCategoryOutput.from(expectedId)));
                final var aCommand = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
                // when
                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenReturn(API.Right(CreateCategoryOutput.from("123")));
                final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpectAll(
                                MockMvcResultMatchers.status().isOk(),
                                MockMvcResultMatchers.header().string("Content-Type",
                                                MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)));
                verify(updateCategoryUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedName, cmd.name())
                                && Objects.equals(expectedDescription, cmd.description())
                                && Objects.equals(expectedIsActive, cmd.isActive())));
        }

        @Test
        public void givenACommandoWithAnInvaliId_whenCallsUpdateCategory_shouldReturnDomainException()
                        throws Exception {
                // given
                final var expectedId = "not-found";
                final var expectedName = "Filmes";
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;

                final var expectedErrorMessage = "Category with ID not-found was not found";

                when(updateCategoryUseCase.execute(any()))
                                .thenThrow(NotFoundException.with(Category.class, CategoryID.from(expectedId)));
                final var aCommand = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
                // when
                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenReturn(API.Right(CreateCategoryOutput.from("123")));
                final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpectAll(
                                MockMvcResultMatchers.status().isNotFound(),
                                MockMvcResultMatchers.header().string("Content-Type",
                                                MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.equalTo(expectedErrorMessage)));
                verify(updateCategoryUseCase, times(1)).execute(argThat(cmd -> Objects.equals(expectedName, cmd.name())
                                && Objects.equals(expectedDescription, cmd.description())
                                && Objects.equals(expectedIsActive, cmd.isActive())));
        }

        @Test
        public void givenACommandWithInvalidId_whenCallsUpdateCategory_shouldReturnNotFoundException()
                        throws Exception {
                // given
                final var expectedId = "123";
                final var expectedName = "Filmes";
                final var expectedDescription = "Categoria mais assistida";
                final var expectedIsActive = true;
                final var expectedErrorMessage = "'name' should not be null";

                when(updateCategoryUseCase.execute(any()))
                                .thenReturn(API.Left(Notification.create(new Error(expectedErrorMessage))));
                final var aCommand = new UpdateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);
                // when
                Mockito.when(cretCreateCategoryUseCase.execute(any()))
                                .thenReturn(API.Right(CreateCategoryOutput.from("123")));
                final var request = MockMvcRequestBuilders.put("/categories/{id}", expectedId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.mapper.writeValueAsString(aCommand));

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpectAll(
                                MockMvcResultMatchers.status().isUnprocessableEntity(),
                                MockMvcResultMatchers.header().string("Content-Type",
                                                MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message",
                                                Matchers.equalTo(expectedErrorMessage)));
        }

        @Test
        public void givenAValidId_whenCallsDeleteCategory_shouldBeOk() throws Exception {
                // given
                final var expectedId = "123";
                doNothing().when(deleteCategoryUseCase).execute(any());
                // when
                final var request = MockMvcRequestBuilders.delete("/categories/{id}", expectedId)
                                .contentType(MediaType.APPLICATION_JSON);
                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpect(MockMvcResultMatchers.status().isNoContent());

                verify(deleteCategoryUseCase, times(1)).execute(Mockito.eq(expectedId));
        }

        @Test
        public void givenAValidParams_whenCallListCategories_shouldReturnCategories() throws Exception {
                // given
                final var expectedPage = 0;
                final var expectedPerPage = 10;
                final var expectedTerms = "movies";
                final var expectedSort = "description";
                final var expectedDirection = "desc";
                final var expectedItemsCount = 1;
                final var expectedTotal = 1;
                final var aCategory = Category.newCategory("Movies", "", true);
                final var expectedItems = List.of(CategoryListOutput.from(aCategory));

                when(listCategoriesUseCase.execute(any())).thenReturn(
                                new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));
                // when
                final var request = MockMvcRequestBuilders.get("/categories")
                                .queryParam("page", String.valueOf(expectedPage))
                                .queryParam("perPage", String.valueOf(expectedPerPage))
                                .queryParam("sort", expectedSort)
                                .queryParam("dir", expectedDirection)
                                .queryParam("search", expectedTerms)
                                .contentType(MediaType.APPLICATION_JSON);
                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // Then
                response.andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.current_page", Matchers.equalTo(expectedPage)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.per_page", Matchers.equalTo(expectedPerPage)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.total", Matchers.equalTo(expectedTotal)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.items", Matchers.hasSize(expectedItemsCount)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id", Matchers.equalTo(aCategory.getId().getValue())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].name", Matchers.equalTo(aCategory.getName())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].description", Matchers.equalTo(aCategory.getDescription())))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].is_active", Matchers.equalTo(aCategory.isActive())));
                verify(listCategoriesUseCase, times(1)).execute(argThat(query -> 
                        Objects.equals(expectedPage, query.page()) &&
                        Objects.equals(expectedPerPage, query.perPage()) &&
                        Objects.equals(expectedDirection, query.direction()) &&
                        Objects.equals(expectedSort, query.sort()) &&
                        Objects.equals(expectedTerms, query.terms())
                ));
        }

}
