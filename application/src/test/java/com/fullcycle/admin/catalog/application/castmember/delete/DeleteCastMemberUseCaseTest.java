package com.fullcycle.admin.catalog.application.castmember.delete;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmeber.CastMember;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class DeleteCastMemberUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultDeleteCastMemberUseCaseTest useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(castMemberGateway);
    }

    @Test
    public void givenAValidId_whenCallsDeleteCastMember_shouldDeleteId(){
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedId = aMember.getId();
        Mockito.doNothing().when(castMemberGateway).deleteById(any());
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteCastMember_shouldBeOk(){
        // given
        final var expectedId = CastMemberId.from("123");
        Mockito.doNothing().when(castMemberGateway).deleteById(any());
        // when
        Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
    }

    @Test
    public void givenAnValidId_whenCallsDeleteCastMemberAndGatewayThrowsException_shouldReceiveException(){
        // given
        final var aMember = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());
        final var expectedId = aMember.getId();
        Mockito.doThrow(new IllegalStateException("Gateway error")).when(castMemberGateway).deleteById(any());
        // when
        Assertions.assertThrows(IllegalStateException.class,() -> useCase.execute(expectedId.getValue()));
        // then
        verify(castMemberGateway).deleteById(eq(expectedId));
    }
}
