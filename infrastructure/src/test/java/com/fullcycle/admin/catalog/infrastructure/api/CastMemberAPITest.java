package com.fullcycle.admin.catalog.infrastructure.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fullcycle.admin.catalog.ControllerTest;
import com.fullcycle.admin.catalog.Fixture;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.create.CreateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.create.DefaultCreateCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.delete.DeleteCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.CastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.retrieve.get.GetCastMemberByIdUseCase;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.CastMemberListOutput;
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberOutput;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.UpdateCastMemberRequest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest(controllers = CastMemberAPI.class)
public class CastMemberAPITest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private DefaultCreateCastMemberUseCase createCastMemberUseCase;

    @MockBean
    private DeleteCastMemberUseCase deleteCastMemberUseCase;

    @MockBean
    private GetCastMemberByIdUseCase getCastMemberByIdUseCase;

    @MockBean
    private ListCastMemberUseCase listCastMemberUseCase;

    @MockBean
    private UpdateCastMemberUseCase updateCastMemberUseCase;

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedId = CastMemberId.from("123456");
        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);
        when(createCastMemberUseCase.execute(any())).thenReturn(CreateCastMemberOutput.from(expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/cast_members").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().string("Location","/cast_members/"+expectedId.getValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId.getValue())));
        verify(createCastMemberUseCase).execute(argThat(cmd ->
                        Objects.equals(expectedName, cmd.name()) &&
                                Objects.equals(expectedType, cmd.type())
                ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCastMember_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "name should not be null";
        final var aCommand = new CreateCastMemberRequest(expectedName, expectedType);
        when(createCastMemberUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));
        // when
        final var aRequest = MockMvcRequestBuilders.post("/cast_members").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1))).andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
        verify(createCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsGetById_shouldReturnIt() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId().getValue();
        when(getCastMemberByIdUseCase.execute(any())).thenReturn(CastMemberOutput.from(aMember));
        // when

        final var aRequest = MockMvcRequestBuilders.get("/cast_members/{id}", expectedId).accept(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk()).andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id",equalTo(expectedId)))
                .andExpect(jsonPath("$.name",equalTo(expectedName)))
                .andExpect(jsonPath("$.type",equalTo(expectedType.name())))
                .andExpect(jsonPath("$.created_at",equalTo(aMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.updated_at",equalTo(aMember.getUpdatedAt().toString())));
        verify(getCastMemberByIdUseCase).execute(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsGetById_shouldReturnNotFound() throws Exception {
        // given
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var expectedId = CastMemberId.from("123");
        when(getCastMemberByIdUseCase.execute(any())).thenThrow(NotFoundException.with(CastMember.class, expectedId));
        // when

        final var aRequest = MockMvcRequestBuilders.get("/cast_members/{id}", expectedId.getValue()).accept(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isNotFound()).andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message",equalTo(expectedErrorMessage)));
        verify(getCastMemberByIdUseCase).execute(eq(expectedId.getValue()));
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCastMember_shouldReturnItsIdentifier() throws Exception {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var aMember = CastMember.newMember(expectedName, expectedType);
        final var expectedId = aMember.getId();
        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);
        when(updateCastMemberUseCase.execute(any())).thenReturn(UpdateCastMemberOutput.from(expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id", Matchers.equalTo(expectedId.getValue())));
        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id()) &&
                Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldReturnNotification() throws Exception {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "name should not be null";
        final var aMember = CastMember.newMember(Fixture.name(), expectedType);
        final var expectedId = aMember.getId();
        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);
        when(updateCastMemberUseCase.execute(any())).thenThrow(NotificationException.with(new Error(expectedErrorMessage)));
        // when
        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity())
                .andExpect(MockMvcResultMatchers.header().string("Location", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors", hasSize(1))).andExpect(jsonPath("$.errors[0].message", equalTo(expectedErrorMessage)));
        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id()) &&
                Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCastMember_shouldReturnNotFound() throws Exception {
        // given
        final var expectedId = CastMemberId.from("123");
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "CastMember with ID 123 was not found";
        final var aCommand = new UpdateCastMemberRequest(expectedName, expectedType);
        when(updateCastMemberUseCase.execute(any())).thenThrow(NotFoundException.with(CastMember.class, expectedId));
        // when
        final var aRequest = MockMvcRequestBuilders.put("/cast_members/{id}", expectedId.getValue()).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(aCommand));
        final var aResponse = this.mvc.perform(aRequest).andDo(MockMvcResultHandlers.print());
        // then
        aResponse.andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.header().string("Location", CoreMatchers.nullValue()))
                .andExpect(MockMvcResultMatchers.header().string("Content-type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message", equalTo(expectedErrorMessage)));
        verify(updateCastMemberUseCase).execute(argThat(cmd ->
                Objects.equals(expectedId.getValue(), cmd.id()) &&
                        Objects.equals(expectedName, cmd.name()) &&
                        Objects.equals(expectedType, cmd.type())
        ));
    }

    @Test
    public void givenAValidId_whenCallsDeleteById_shouldDeleteIt() throws Exception {
        // given
        final var expectedId = "123";

        doNothing().when(deleteCastMemberUseCase).execute(any());
        // when
        final var aRequest = MockMvcRequestBuilders.delete("/cast_members/{id}",expectedId).accept(MediaType.APPLICATION_JSON);
        final var response =  this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isNoContent());
        verify(deleteCastMemberUseCase).execute(eq(expectedId));

    }

    @Test
    public void givenValidParams_whenCallListCastMembers_shouldReturnIt() throws Exception {
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedPage = 1;
        final var expectedPerPage = 20;
        final var expectedTerms = "Alg";
        final var expectedSort = "name";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(CastMemberListOutput.from(aMember));
        when(listCastMemberUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));
        // when
        final var aRequest = MockMvcRequestBuilders.get("/cast_members").queryParam("page",String.valueOf(expectedPage))
                .queryParam("perPage",String.valueOf(expectedPerPage))
                .queryParam("search",expectedTerms)
                .queryParam("sort",expectedSort)
                .queryParam("dir", expectedDirection)
                .accept(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page",equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page",equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total",equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items",hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id",equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].type",equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at",equalTo(aMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].name",equalTo(aMember.getName())));
        verify(listCastMemberUseCase).execute(argThat(aquery ->
                Objects.equals(expectedPage, aquery.page())
                        && Objects.equals(expectedPerPage, aquery.perPage())
                        && Objects.equals(expectedTerms, aquery.terms())
                        && Objects.equals(expectedSort, aquery.sort())
                        && Objects.equals(expectedDirection, aquery.direction())
                ));
    }

    @Test
    public void givenEmptyParams_whenCallListCastMembers_shouldUseDefaultAndReturnIt() throws Exception {
// given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "desc";
        final var expectedItemsCount = 1;
        final var expectedTotal = 1;
        final var expectedItems = List.of(CastMemberListOutput.from(aMember));
        when(listCastMemberUseCase.execute(any())).thenReturn(new Pagination<>(expectedPage, expectedPerPage, expectedTotal, expectedItems));
        // when
        final var aRequest = MockMvcRequestBuilders.get("/cast_members").accept(MediaType.APPLICATION_JSON);
        final var response = this.mvc.perform(aRequest);
        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.current_page",equalTo(expectedPage)))
                .andExpect(jsonPath("$.per_page",equalTo(expectedPerPage)))
                .andExpect(jsonPath("$.total",equalTo(expectedTotal)))
                .andExpect(jsonPath("$.items",hasSize(expectedItemsCount)))
                .andExpect(jsonPath("$.items[0].id",equalTo(aMember.getId().getValue())))
                .andExpect(jsonPath("$.items[0].type",equalTo(aMember.getType().name())))
                .andExpect(jsonPath("$.items[0].created_at",equalTo(aMember.getCreatedAt().toString())))
                .andExpect(jsonPath("$.items[0].name",equalTo(aMember.getName())));
        verify(listCastMemberUseCase).execute(argThat(aquery ->
                Objects.equals(expectedPage, aquery.page())
                        && Objects.equals(expectedPerPage, aquery.perPage())
                        && Objects.equals(expectedTerms, aquery.terms())
                        && Objects.equals(expectedSort, aquery.sort())
                        && Objects.equals(expectedDirection, aquery.direction())
        ));
    }

}
