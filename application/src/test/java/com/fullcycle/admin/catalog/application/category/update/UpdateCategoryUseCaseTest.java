package com.fullcycle.admin.catalog.application.category.update;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {
    @InjectMocks
    private DefaultUpdateCategoryUseCase useCase;
    @Mock
    private CategoryGateway categoryGateway;

     @BeforeEach
    void cleanUp(){
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory("Film", null, true);
        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);
        Mockito.when(categoryGateway
                .findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(aCategory.clone()));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        final var actualOutput = useCase.execute(aCommand).get();
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1))
                .update(argThat(aUpdatedCategory -> Objects.equals(expectedName, aUpdatedCategory.getName())
                        && Objects.equals(expectedDescription, aUpdatedCategory.getDescription())
                        && Objects.equals(expectedIsActive, aUpdatedCategory.isActive())
                        && Objects.equals(expectedId, aUpdatedCategory.getId())
                        && Objects.equals(aCategory.getCreatedAt(), aUpdatedCategory.getCreatedAt())
                        && aCategory.getUpdatedAt().isBefore(aUpdatedCategory.getUpdatedAt())
                        && Objects.isNull(aUpdatedCategory.getDeletedAt())));
    }

    @Test
    public void givenAnInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var aCategory = Category.newCategory("Film", null, true);
        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);
        Mockito.when(categoryGateway
                .findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(aCategory.clone()));
        final var notification = useCase.execute(aCommand).getLeft();
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Mockito.verify(categoryGateway, times(0)).update(any());
    }

    @Test
    public void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnAnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = false;
        final var aCategory = Category.newCategory("Film", expectedDescription, true);
        Assertions.assertTrue(aCategory.isActive());
        Assertions.assertNull(aCategory.getDeletedAt());
        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);
        Mockito.when(categoryGateway
                .findById(Mockito.eq(expectedId)))
                .thenReturn(Optional.of(aCategory.clone()));
        Mockito.when(categoryGateway.update(Mockito.any())).thenAnswer(returnsFirstArg());
        final var actualOutput = useCase.execute(aCommand).get();
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));
        Mockito.verify(categoryGateway, times(1))
                .update(argThat(aUpdatedCategory -> Objects.equals(expectedName, aUpdatedCategory.getName())
                        && Objects.equals(expectedDescription, aUpdatedCategory.getDescription())
                        && Objects.equals(expectedIsActive, aUpdatedCategory.isActive())
                        && Objects.equals(expectedId, aUpdatedCategory.getId())
                        && Objects.equals(aCategory.getCreatedAt(), aUpdatedCategory.getCreatedAt())
                        && aCategory.getUpdatedAt().isBefore(aUpdatedCategory.getUpdatedAt())
                        && Objects.nonNull(aUpdatedCategory.getDeletedAt())));
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final var aCategory = Category.newCategory("Film", null, true);
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var exceptionErrorMessage = "Gateway Error";
        final var expectedId = aCategory.getId();
        final var aCommand = UpdateCategoryCommand.with(
                expectedId.getValue(),
                expectedName,
                expectedDescription,
                expectedIsActive);
        when(categoryGateway
                .findById(eq(expectedId)))
                .thenReturn(Optional.of(aCategory.clone()));
        when(categoryGateway.update(any()))
                .thenThrow(new IllegalStateException(exceptionErrorMessage));
        final var notification = useCase.execute(aCommand).getLeft();
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(exceptionErrorMessage, notification.firstError().message());
        Mockito.verify(categoryGateway, times(1)).update(any());
    }

    @Test
    public void givenACommandWithInvalidId_whenCallsUpdateCategory_shouldReturnNotFoundException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = false;
        final var aCategory = Category.newCategory("Film", expectedDescription, true);
        Assertions.assertTrue(aCategory.isActive());
        Assertions.assertNull(aCategory.getDeletedAt());
        final var expectedId = "126384684";
        final var expectedErrorMessage = "Category with ID 126384684 was not found";
        final var expectedErrorCount = 1;
        final var aCommand = UpdateCategoryCommand.with(
                expectedId,
                expectedName,
                expectedDescription,
                expectedIsActive);
        when(categoryGateway
                .findById(eq(CategoryID.from(expectedId))))
                .thenReturn(Optional.empty());
        final var actualException = Assertions.assertThrows(NotFoundException.class, ()->useCase.execute(aCommand));
        System.out.println(actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Mockito.verify(categoryGateway, times(1)).findById(eq(CategoryID.from(expectedId)));
        Mockito.verify(categoryGateway, times(0))
                .update(any());
    }
}
