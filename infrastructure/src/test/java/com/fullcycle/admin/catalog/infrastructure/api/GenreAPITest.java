package com.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var expectedId = "123";
        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        Mockito.when(createGenreUseCase.execute(Mockito.any())).thenReturn(CreateGenreOutput.from(expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location","/genres/%s".formatted(expectedId)))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)));
        Mockito.verify(createGenreUseCase).execute(Mockito.argThat(cmd ->
                        Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
                ));
    }

    @Test
    public void givenAInvalidName_whenCallsCreateGenre_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        Mockito.when(createGenreUseCase.execute(Mockito.any())).thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location",Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
        Mockito.verify(createGenreUseCase).execute(Mockito.argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() {

    }
}
