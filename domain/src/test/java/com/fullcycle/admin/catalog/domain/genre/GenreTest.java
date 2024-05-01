package com.fullcycle.admin.catalog.domain.genre;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;

import java.util.ArrayList;
import java.util.List;

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
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenInvalidNullName_whenCallNewGenreAndValid_shouldThrowError() {
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be null";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            Genre.newGenre(expectedName, expectedIsActive);
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenInvalidEmptyName_whenCallNewGenreAndValid_shouldThrowError() {
        final String expectedName = "";
        final var expectedIsActive = true;
        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            Genre.newGenre(expectedName, expectedIsActive);
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
        final var expectedErrorMessage = "'name' must be between 1 and 255 characters";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            Genre.newGenre(expectedName, expectedIsActive);
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAnActiveGenre_whenCallDeactivate_shouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = 0;
        final var actualGenre = Genre.newGenre(expectedName, true);

        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertNull(actualGenre.getDeletedAt());

        actualGenre.deactivate();

        Assertions.assertNotNull(actualGenre);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInactiveGenre_whenCallActivate_shouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = 0;
        final var actualGenre = Genre.newGenre(expectedName, false);

        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getDeletedAt());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.activate();

        Assertions.assertNotNull(actualGenre);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories().size());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualCreatedAd.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallUpdate_shouldReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var actualGenre = Genre.newGenre("acao", false);
        final var expectedCategories = List.of(CategoryID.from("123"));
        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getDeletedAt());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getCreatedAt());

        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallUpdateWithInactive_shouldReceiveGenreUpdated() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var actualGenre = Genre.newGenre("acao", true);
        final var expectedCategories = List.of(CategoryID.from("123"));
        Assertions.assertTrue(actualGenre.isActive());
        Assertions.assertNull(actualGenre.getDeletedAt());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();

        actualGenre.update(expectedName, expectedIsActive, expectedCategories);

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenre_whenCallUpdateWithEmptyName_shouldThrowError() {
        final var expectedName = "";
        final var expectedIsActive = true;
        final var actualGenre = Genre.newGenre("acao", false);
        final var expectedCategories = List.of(CategoryID.from("123"));
        Assertions.assertFalse(actualGenre.isActive());
        Assertions.assertNotNull(actualGenre.getDeletedAt());

        final var expectedErrorCount = 1;
        final var expectedErrorMessage = "'name' should not be empty";
        final var actualException = Assertions.assertThrows(NotificationException.class, () -> {
            actualGenre.update(expectedName, expectedIsActive, expectedCategories);
        });
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
    }

    @Test
    public void givenAValidGenre_whenCallUpdateWithNullCategories_shouldThrowError() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<CategoryID>();
        final var actualGenre = Genre.newGenre("acao", expectedIsActive);
        final var actualCreatedAd = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();
        Assertions.assertDoesNotThrow(() -> {
            actualGenre.update(expectedName, expectedIsActive, null);
        });

        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidEmptyCategoriesGenre_whenCallAddCategory_shouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("321");
        final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();
        actualGenre.addCategory(seriesID);
        actualGenre.addCategory(moviesID);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAValidCategoriesGenreWith2Categories_whenCallRemoveCategory_shouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("321");
        final List<CategoryID> expectedCategories = List.of(moviesID);
        final var actualGenre = Genre.newGenre("acao", expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        final var actualUpdatedAt = actualGenre.getUpdatedAt();
        actualGenre.update(expectedName, expectedIsActive, List.of(seriesID, moviesID));
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        actualGenre.removeCategory(seriesID);
        Assertions.assertEquals(1, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertTrue(actualUpdatedAt.isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullAsCategoryId_whenCallAddCategory_shouldReceiveOK() throws InterruptedException {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<CategoryID>();
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.addCategory(null);
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAnInvalidNullAsCategoryId_whenCallRemoveCategory_shouldReceiveOK() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("321");
        final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);
        final var actualGenre = Genre.newGenre("acao", expectedIsActive);
        actualGenre.update(expectedName, expectedIsActive, expectedCategories);
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.removeCategory(null);
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAvalidCategoriesGenre_whenCallAddCategories_shouldReceiveOK() throws InterruptedException {
        final var seriesID = CategoryID.from("123");
        final var moviesID = CategoryID.from("321");
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = List.of(seriesID, moviesID);
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.addCategories(expectedCategories);
        Assertions.assertEquals(2, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenAvalidEmptyCategoriesGenre_whenCallAddCategories_shouldReceiveOK() throws InterruptedException {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<CategoryID>();
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.addCategories(expectedCategories);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

    @Test
    public void givenNullCategoriesGenre_whenCallAddCategories_shouldReceiveOK() throws InterruptedException {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final List<CategoryID> expectedCategories = new ArrayList<CategoryID>();
        final var actualGenre = Genre.newGenre(expectedName, expectedIsActive);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        final var actualCreatedAd = actualGenre.getCreatedAt();
        actualGenre.addCategories(null);
        Assertions.assertEquals(0, actualGenre.getCategories().size());
        Assertions.assertNotNull(actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(actualCreatedAd, actualGenre.getCreatedAt());
        Assertions.assertNotNull(actualGenre.getCreatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }

}
