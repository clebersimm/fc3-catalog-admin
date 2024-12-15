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
import com.fullcycle.admin.catalog.application.castmember.retrieve.list.ListCastMemberUseCase;
import com.fullcycle.admin.catalog.application.castmember.update.UpdateCastMemberUseCase;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.castmember.models.CreateCastMemberRequest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
}
