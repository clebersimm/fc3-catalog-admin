package com.fullcycle.admin.catalog.application.genre.retrieve.get;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.application.genre.retrive.get.GetGenreByIdUseCase;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
public class GetGenreByIdUseCaseIT {
    @Autowired
    private GetGenreByIdUseCase useCase;
    @Autowired
    private GenreGateway genreGateway;
    @Autowired
    private CategoryGateway categoryGateway;

    @Test
    public void givenAValidId_whenCallsGetGenre_shouldReturnGenre() {
        // given
        final var filmes = categoryGateway.create(Category.newCategory("Filmes","", true));
        final var series = categoryGateway.create(Category.newCategory("Series","", true));
        final var expectedName = "Ação";
        final var isActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());
        final var aGenre = genreGateway.create(Genre.newGenre(expectedName, isActive).addCategories(expectedCategories));
        final var expectedId = aGenre.getId();
        // when
        final var actualGenre = useCase.execute(expectedId.getValue());
        // then
        Assertions.assertEquals(expectedId.getValue(), actualGenre.id());
        Assertions.assertEquals(expectedName, actualGenre.name());
        Assertions.assertEquals(isActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.categories().size() &&
                asString(expectedCategories).containsAll(actualGenre.categories())
        );
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.createdAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.updatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.deletedAt());
    }

    @Test
    public void givenAValidId_whenCallsGetGenreAndNotExists_shouldReturnNotFound() {
        // given
        final var expectedErrorMessage = "Genre with ID 123 was not found";
        final var expectedId = GenreID.from("123");
        // when
        final var actualException = Assertions.assertThrows(NotFoundException.class,
                () -> useCase.execute(expectedId.getValue()));
        // then
        Assertions.assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream().map(CategoryID::getValue).toList();
    }
}
