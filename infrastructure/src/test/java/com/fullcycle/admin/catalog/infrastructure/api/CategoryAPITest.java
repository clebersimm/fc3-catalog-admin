package com.fullcycle.admin.catalog.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Objects;

import org.hamcrest.Matchers;
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

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() throws Exception {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var aInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(cretCreateCategoryUseCase.execute(any()))
                .thenReturn(API.Right(CreateCategoryOutput.from("123")));
        final var request = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isCreated(),
                        MockMvcResultMatchers.header().string("location", "/categories/123"),
                        MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        MockMvcResultMatchers.jsonPath("$.id",Matchers.equalTo("123")));
        verify(cretCreateCategoryUseCase, times(1)).execute(argThat(
                cmd -> Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnNotification() throws Exception {
        final String expectedName = null;
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedMessage = "'name' should not be null";
        final var aInput = new CreateCategoryApiInput(expectedName, expectedDescription, expectedIsActive);

        Mockito.when(cretCreateCategoryUseCase.execute(any()))
                .thenReturn(API.Left(Notification.create(new Error((expectedMessage)))));
        final var request = MockMvcRequestBuilders.post("/categories").contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aInput));

        this.mvc.perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpectAll(
                        MockMvcResultMatchers.status().isUnprocessableEntity(),
                        MockMvcResultMatchers.header().string("location", Matchers.nullValue()),
                        MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        MockMvcResultMatchers.jsonPath("$.errors",Matchers.hasSize(1)),
                        MockMvcResultMatchers.jsonPath("$.errors[0].message",Matchers.equalTo(expectedMessage)));
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
                        MockMvcResultMatchers.header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE),
                        MockMvcResultMatchers.jsonPath("$.errors",Matchers.hasSize(1)),
                        MockMvcResultMatchers.jsonPath("$.errors[0].message",Matchers.equalTo(expectedMessage)));
        verify(cretCreateCategoryUseCase, times(1)).execute(argThat(
                cmd -> Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedDescription, cmd.description())
                        && Objects.equals(expectedIsActive, cmd.isActive())));
    }
}
