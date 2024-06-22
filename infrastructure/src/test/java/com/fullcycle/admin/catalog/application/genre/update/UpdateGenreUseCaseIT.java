package com.fullcycle.admin.catalog.application.genre.update;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@IntegrationTest
public class UpdateGenreUseCaseIT {
    @Autowired
    private DefaultUpdateGenreUseCase useCase;
    @SpyBean
    private CategoryGateway categoryGateway;
    @SpyBean
    private GenreGateway genreGateway;
    @Autowired
    private GenreRepository genreRepository;
    @Test
    public void givenAValidCommand_whenCallsCreateUpdateGenre_shouldReturnGenreId() {
        // given
        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));
        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var aCommand = UpdateGenreCommand.with(expectedId.getValue(),
                expectedName,
                expectedIsActive,
                asString(expectedCategories));
        // when
        final var actualOutput = useCase.execute(aCommand);
        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
        final var actualGenre = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDS().size() &&
                        expectedCategories.containsAll(actualGenre.getCategories())
                );
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getUpdatedAt());
    }

    @Test
    public void givenAValidCommandWithCategories_whenCallsCreateUpdateGenre_shouldReturnGenreId() {
        // given
        final var filmes = categoryGateway.create(Category.newCategory("Filmes","",true));
        final var series = categoryGateway.create(Category.newCategory("Séries","",true));
        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));
        final var expectedId = aGenre.getId();
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of(filmes.getId(), series.getId());
        final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive,
                asString(expectedCategories));

        // when
        final var actualOutput = useCase.execute(aCommand);
        // then
        Assertions.assertNotNull(actualOutput);
        Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
        final var actualGenre = genreRepository.findById(aGenre.getId().getValue()).get();

        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertTrue(
                expectedCategories.size() == actualGenre.getCategoryIDS().size() &&
                        expectedCategories.containsAll(actualGenre.getCategories())
        );
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getUpdatedAt());
    }

    @Test
    public void givenAInvalidName_whenCallsCreateUpdateGenre_shouldReturnNotificationException() {
        // given
        final var aGenre = genreGateway.create(Genre.newGenre("acao", true));
        final var expectedId = aGenre.getId();
        final String expectedName = null;
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var expectedErrorMessage = "'name' should not be null";
        final var expectedErrorCount = 1;
        final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive,
                asString(expectedCategories));
        // when
        final var actualException = Assertions.assertThrows(NotificationException.class,
                () -> {
                    useCase.execute(aCommand);
                });
        // then
        Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
        Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
        Mockito.verify(genreGateway, times(1)).findById(expectedId);
        Mockito.verify(categoryGateway, times(0)).findById(any());
        Mockito.verify(genreGateway, times(0)).update(any());
    }

    private List<String> asString(final List<CategoryID> ids) {
        return ids.stream().map(CategoryID::getValue).toList();
    }
}
