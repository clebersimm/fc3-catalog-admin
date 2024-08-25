package com.fullcycle.admin.catalog.application.castmember.create;

import com.fullcycle.admin.catalog.application.Fixture;
import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.castmeber.CastMemberGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

public class CreateCastMemberUseCaseTest extends UseCaseTest {

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

        final var aCommand = CreateCastMemnberCommand.with(
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
}
