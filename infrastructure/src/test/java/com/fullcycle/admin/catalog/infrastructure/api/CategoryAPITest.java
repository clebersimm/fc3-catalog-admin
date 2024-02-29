package com.fullcycle.admin.catalog.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.fullcycle.admin.catalog.application.category.retrive.get.CategoryOutput;
import com.fullcycle.admin.catalog.application.category.retrive.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.get.GetCategoryByIdUseCaseIT;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;

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
                final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON);
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
                                .thenThrow(DomainException.with(
                                                new Error("Category with ID %s was not found".formatted(expectedId))));
                // when
                final var request = MockMvcRequestBuilders.get("/categories/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON);

                final var response = this.mvc.perform(request)
                                .andDo(MockMvcResultHandlers.print());
                // then
                response.andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.equalTo(expectedErrorMessage)));
        }

}
