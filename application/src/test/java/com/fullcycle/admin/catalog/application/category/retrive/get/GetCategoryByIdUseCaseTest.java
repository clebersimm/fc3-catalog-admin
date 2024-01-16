package com.fullcycle.admin.catalog.application.category.retrive.get;

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

@ExtendWith(MockitoExtension.class)
public class GetCategoryByIdUseCaseTest {
    @InjectMocks
    private DefaultGetCategoryByIdUseCase useCase;
    @Mock
    private CategoryGateway categoryGateway;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(categoryGateway);
    }

    @Test
    public void givenAValidId_whenCallsGetCategory_shouldReturnACategory() {
        final var aCategory = Category.newCategory("Filmes", "A categoria", true);
        final var expectedId = aCategory.getId();
        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.of(aCategory.clone()));
        final var actualCategory = useCase.execute(expectedId.getValue());
        Assertions.assertEquals(CategoryOutput.from(aCategory), actualCategory);
        // TODO pode ser colocado para comparar todos os valores
    }

    @Test
    public void givenAInvalidId_whenCallGetCategory_shouldRetunNotFound() {
        final var expectedErrorMessage = "Category with ID 1234 was not found";
        final var expectedId = CategoryID.from("1234");
        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenReturn(Optional.empty());
        final var actualException = Assertions.assertThrows(DomainException.class,
                () -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    public void givenAValidId_whenGatewayThrowsError_shouldReturnException() {
        final var expectedErrorMessage = "Gateway error";
        final var expectedId = CategoryID.from("1234");
        Mockito.when(categoryGateway.findById(Mockito.eq(expectedId))).thenThrow(new IllegalStateException(expectedErrorMessage));
        final var actualException = Assertions.assertThrows(IllegalStateException.class,
                () -> useCase.execute(expectedId.getValue()));
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }
}
