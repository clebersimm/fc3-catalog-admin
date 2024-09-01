package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberType;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CreateCastMemberUseCaseTest extends UseCaseTest {

    @InjectMocks
    private DefaultCreateCastMemberUseCase useCase;

    @Mock
    private CastMemberGateway castMemberGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of();
    }

    @Test
    public void givenAValidCommand_whenCallsCreateCastMember_shouldReturnIt() {
        // given
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var aCommand = CreateCastMemberCommand.with(
                expectedName, expectedType
        );
        Mockito.when(castMemberGateway.create(any())).thenAnswer(returnsFirstArg());
        // when
        final var actualOutput = useCase.execute(aCommand);
        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        verify(castMemberGateway).create(argThat(aCastMember ->
                Objects.nonNull(aCastMember.getId())
                && Objects.equals(expectedName, aCastMember.getName())
                && Objects.equals(expectedType, aCastMember.getType())
                && Objects.nonNull(aCastMember.getCreatedAt())
                && Objects.nonNull(aCastMember.getUpdatedAt())
        ));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        // given
        final String expectedName = null;
        final var expectedType = Fixture.CastMember.type();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var aCommand = CreateCastMemberCommand.with(
                expectedName, expectedType
        );
        // when
        final var actualException = Assertions.assertThrows(NotificationException.class,()->useCase.execute(aCommand)) ;
        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(castMemberGateway, times(0)).create(any());
    }

    @Test
    public void givenAnInvalidType_whenCallsCreateCastMember_shouldThrowsNotificationException() {
        // given
        final var expectedName = Fixture.name();
        final CastMemberType expectedType = null;
        final var expectedErrorMessage = "'type' should not be null";
        final var expectedErrorCount = 1;
        final var aCommand = CreateCastMemberCommand.with(
                expectedName, expectedType
        );
        // when
        final var actualException = Assertions.assertThrows(NotificationException.class,()->useCase.execute(aCommand)) ;
        // then
        Assertions.assertNotNull(actualException);
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        verify(castMemberGateway, times(0)).create(any());
    }
}
