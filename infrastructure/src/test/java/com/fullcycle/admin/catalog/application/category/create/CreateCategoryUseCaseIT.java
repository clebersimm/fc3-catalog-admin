package com.fullcycle.admin.catalog.application.category.create;

import static org.mockito.Mockito.times;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
public class CreateCategoryUseCaseIT {
    @Autowired
    private DefaultCreateCategoryUseCase useCase;
    @Autowired
    private CategoryRepository categoryRepository;

    //Neste caso sendo utilizado para verificar dentro do bean-spring
    @SpyBean
    private CategoryGateway categoryGateway;


    @Test
    public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        Assertions.assertEquals(0, categoryRepository.count());
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(aCommand).get();
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(1, categoryRepository.count());
        final var actualCategory = categoryRepository.findById(actualOutput.id()).get();
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAnInvalidName_whenCallsCreateCategory_thenShouldReturnDomainException() {
        final String expectedName = null;
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        final var notification = useCase.execute(aCommand).getLeft();
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, notification.firstError().message());
        Assertions.assertEquals(0, categoryRepository.count());
        Mockito.verify(categoryGateway, times(0)).create(any());
    }

    @Test
    public void givenAValidCommandWithInactiveCategory_whenCallsCreateCategory_shouldReturnInactiveCategoryId() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = false;
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        final var actualOutput = useCase.execute(aCommand).get();
        Assertions.assertNotNull(actualOutput);
        Assertions.assertNotNull(actualOutput.id());
        Assertions.assertEquals(1, categoryRepository.count());
        final var actualCategory = categoryRepository.findById(actualOutput.id()).get();
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertNotNull(actualCategory.getCreatedAt());
        Assertions.assertNotNull(actualCategory.getUpdatedAt());
        Assertions.assertNotNull(actualCategory.getDeletedAt());
    }

    @Test
    public void givenAValidCommand_whenGatewayThrowsRandomException_shouldReturnException() {
        final var expectedName = "Filmes";
        final var expectedDescription = "Categoria mais assistida";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var exceptionErrorMessage = "Gateway Error";
        final var aCommand = CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);
        Mockito.doThrow(new IllegalStateException(exceptionErrorMessage))
            .when(categoryGateway).create(any());
        final var notification = useCase.execute(aCommand).getLeft();
        Assertions.assertEquals(expectedErrorCount, notification.getErrors().size());
        Assertions.assertEquals(exceptionErrorMessage, notification.firstError().message());
    }
}
