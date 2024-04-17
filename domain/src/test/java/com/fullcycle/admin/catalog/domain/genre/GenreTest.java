package com.fullcycle.admin.catalog.domain.genre;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.validation.handler.ThrowsValidationHandler;

public class GenreTest {

    @Test
    public void givenValidParams_whenCallNewGenre_shouldInstantiateAGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertNotNull(actualGenre);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
    }

    @Test
    public void givenInvalidNullName_whenCallNewGenreAndValid_shouldThrowError() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "name should not be null";
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> {
            actualGenre.validate(new ThrowsValidationHandler());
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidEmptyName_whenCallNewGenreAndValid_shouldThrowError() {
        final String expectedName = "";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "name should not be empty";
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> {
            actualGenre.validate(new ThrowsValidationHandler());
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidNameLenghtGT255_whenCallNewGenreAndValid_shouldThrowError() {
        final String expectedName = """
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test test test
                test test test test
                    """;
        ;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "name must be between 1 and 255 characters";
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        final var actualException = Assertions.assertThrows(DomainException.class, () -> {
            actualGenre.validate(new ThrowsValidationHandler());
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }
}
