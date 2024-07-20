package com.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.create.CreateGenreUseCase;
import com.fullcycle.admin.catalog.application.genre.retrive.get.GenreOutput;
import com.fullcycle.admin.catalog.application.genre.retrive.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreOutput;
import com.fullcycle.admin.catalog.application.genre.update.UpdateGenreUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.genre.models.CreateGenreRequest;
import com.fullcycle.admin.catalog.infrastructure.genre.models.UpdateGenreRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.function.RequestPredicates.headers;

@ControllerTest(controllers = GenreAPI.class)
public class GenreAPITest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CreateGenreUseCase createGenreUseCase;

    @MockBean
    private GetGenreByIdUseCase getGenreByIdUseCase;

    @MockBean
    private UpdateGenreUseCase updateGenreUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateGenre_shouldReturnGenreId() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var expectedId = "123";
        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        Mockito.when(createGenreUseCase.execute(any())).thenReturn(CreateGenreOutput.from(expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location","/genres/%s".formatted(expectedId)))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)));
        verify(createGenreUseCase).execute(Mockito.argThat(cmd ->
                        Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
                ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateGenre_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var aCommand = new CreateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        Mockito.when(createGenreUseCase.execute(any())).thenThrow(new NotificationException("Error", Notification.create(new Error(expectedErrorMessage))));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location",Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
        verify(createGenreUseCase).execute(Mockito.argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetGenreById_shouldReturnGenre() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = false;
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive).addCategories(expectedCategories.stream().map(CategoryID::from).toList());
        final var expectedId = aGenre.getId().getValue();
        Mockito.when(getGenreByIdUseCase.execute(any())).thenReturn(GenreOutput.from(aGenre));
        // when
        final var aRequest = MockMvcRequestBuilders.get("/genres/{id}",expectedId).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id",Matchers.equalTo(expectedId)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",Matchers.equalTo(expectedName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.categories_id",Matchers.equalTo(expectedCategories)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.is_active",Matchers.equalTo(expectedIsActive)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.created_at",Matchers.equalTo(aGenre.getCreatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.updated_at",Matchers.equalTo(aGenre.getUpdatedAt().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.deleted_at",Matchers.equalTo(aGenre.getDeletedAt().toString())));
        verify(getGenreByIdUseCase).execute(ArgumentMatchers.eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetGenreById_shouldReturnNotFound() throws Exception {
        // given
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var expectedId = GenreID.from("123");

        Mockito.when(getGenreByIdUseCase.execute(any())).thenThrow(NotFoundException.with(Genre.class, expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.get("/genres/{id}",expectedId.getValue()).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message",Matchers.equalTo(expectedErrorMessage)));
        verify(getGenreByIdUseCase).execute(ArgumentMatchers.eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateGenre_shouldReturnGenreId() throws Exception {
        // given
        final var expectedName = "Ação";
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = aGenre.getId().getValue();

        final var aCommand = new UpdateGenreRequest(expectedName, expectedCategories, expectedIsActive);
        Mockito.when(updateGenreUseCase.execute(any())).thenReturn(UpdateGenreOutput.from(aGenre));

        // when
        final var aRequest = MockMvcRequestBuilders.put("/genres", expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.equalTo(expectedId)));
        verify(updateGenreUseCase).execute(Mockito.argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateGenre_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedCategories = List.of("123","321");
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var aGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var expectedId = aGenre.getId().getValue();

        final var aCommand = new UpdateGenreRequest("Ação", expectedCategories, expectedIsActive);
        Mockito.when(updateGenreUseCase.execute(any()))
                .thenThrow(new NotificationException("Error",Notification.create(new Error(expectedErrorMessage))));
        // when
        final var aRequest = MockMvcRequestBuilders.put("/genres/{id}",expectedId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location",Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-Type",MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0].message", Matchers.equalTo(expectedErrorMessage)));
        verify(updateGenreUseCase).execute(Mockito.argThat(cmd ->
                Objects.equals(expectedName, cmd.name())
                        && Objects.equals(expectedIsActive, cmd.isActive())
                        && Objects.equals(expectedCategories, cmd.categories())
        ));
    }
}
