package com.fullcycle.admin.catalog.application.category.create;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCategoryUseCaseTest {

    @InjectMocks
    private DefaultCreateCategoryUseCase useCase;
    @Mock
    private CategoryGateway gateway;

    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        when(gateway.create(any()))
                .thenAnswer(returnsFirstArg());
        final var actualOutput = useCase.execute(aCommand);
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        verify(gateway, times(1))
                .create(argThat(aCategory -> Objects.equals(expectedName, aCategory.getName())
                        && Objects.equals(expectedDescription, aCategory.getDescription())
                        && Objects.equals(expectedIsActive, aCategory.isActive())
                        && Objects.nonNull(aCategory.getId())
                        && Objects.nonNull(aCategory.getCreatedAt())
                        && Objects.nonNull(aCategory.getUpdatedAt())
                        && Objects.isNull(aCategory.getDeletedAt())));
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> useCase.execute(aCommand));
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
        Mockito.verify(gateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = false;
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        when(gateway.create(any()))
                .thenAnswer(returnsFirstArg());
        final var actualOutput = useCase.execute(aCommand);
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        verify(gateway, times(1))
                .create(argThat(aCategory -> Objects.equals(expectedName, aCategory.getName())
                        && Objects.equals(expectedDescription, aCategory.getDescription())
                        && Objects.equals(expectedIsActive, aCategory.isActive())
                        && Objects.nonNull(aCategory.getId())
                        && Objects.nonNull(aCategory.getCreatedAt())
                        && Objects.nonNull(aCategory.getUpdatedAt())
                        && Objects.nonNull(aCategory.getDeletedAt())));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var exceptionErrorMessage = "Gateway Error";
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        when(gateway.create(any()))
                .thenThrow(new IllegalStateException(exceptionErrorMessage));
        final var actualException = Assertions.assertThrows(IllegalStateException.class,
                () -> useCase.execute(aCommand));
        Assertions.assertEquals(exceptionErrorMessage, actualException.getMessage());
        Mockito.verify(gateway, times(1)).create(any());
    }
}