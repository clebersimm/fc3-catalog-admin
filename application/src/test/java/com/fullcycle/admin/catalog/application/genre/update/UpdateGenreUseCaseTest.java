package com.fullcycle.admin.catalog.application.genre.update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;

@ExtendWith(MockitoExtension.class)
public class UpdateGenreUseCaseTest {
        @InjectMocks
        private DefaultUpdateGenreUseCase useCase;
        @Mock
        private CategoryGateway categoryGateway;
        @Mock
        private GenreGateway genreGateway;

        @Test
        public void givenAValidCommand_whenCallsCreateUpdateGenre_shouldReturnGenreId() {
                // given
                final var aGenre = Genre.newGenre("acao", true);
                final var expectedId = aGenre.getId();
                final var expectedName = "Ação";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of();
                final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive,
                                asString(expectedCategories));
                Mockito.when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
                Mockito.when(genreGateway.update(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
                Mockito.verify(genreGateway, times(1)).findById(expectedId);
                Mockito.verify(genreGateway, times(1))
                                .update(Mockito.argThat(aUpdatedGenre -> Objects.equals(expectedName,
                                                aUpdatedGenre.getId())
                                                && Objects.equals(expectedName, aUpdatedGenre.getName())
                                                && Objects.equals(expectedIsActive, aUpdatedGenre.isActive())
                                                && Objects.equals(expectedCategories, aUpdatedGenre.getCategories())
                                                && Objects.equals(aGenre.getCreatedAt(), aUpdatedGenre.getCreatedAt())
                                                && aGenre.getUpdatedAt().isBefore(aUpdatedGenre.getUpdatedAt())
                                                && Objects.isNull(aUpdatedGenre.getDeletedAt())));
        }

        @Test
        public void givenAValidCommandWithCategories_whenCallsCreateUpdateGenre_shouldReturnGenreId() {
                // given
                final var aGenre = Genre.newGenre("acao", true);
                final var expectedId = aGenre.getId();
                final var expectedName = "Ação";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of(CategoryID.from("123"), CategoryID.from("456"));
                final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive,
                                asString(expectedCategories));
                Mockito.when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
                Mockito.when(categoryGateway.existsById(any())).thenReturn(expectedCategories);
                Mockito.when(genreGateway.update(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
                Mockito.verify(genreGateway, times(1)).findById(expectedId);
                Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategories));
                Mockito.verify(genreGateway, times(1))
                                .update(Mockito.argThat(aUpdatedGenre -> Objects.equals(expectedName,
                                                aUpdatedGenre.getId())
                                                && Objects.equals(expectedName, aUpdatedGenre.getName())
                                                && Objects.equals(expectedIsActive, aUpdatedGenre.isActive())
                                                && Objects.equals(expectedCategories, aUpdatedGenre.getCategories())
                                                && Objects.equals(aGenre.getCreatedAt(), aUpdatedGenre.getCreatedAt())
                                                && aGenre.getUpdatedAt().isBefore(aUpdatedGenre.getUpdatedAt())
                                                && Objects.isNull(aUpdatedGenre.getDeletedAt())));
        }

        @Test
        public void givenAInvalidName_whenCallsCreateUpdateGenre_shouldReturnNotificationException() {
                // given
                final var aGenre = Genre.newGenre("acao", true);
                final var expectedId = aGenre.getId();
                final var expectedName = "Ação";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of(CategoryID.from("123"), CategoryID.from("456"));
                final var aCommand = UpdateGenreCommand.with(expectedId.getValue(), expectedName, expectedIsActive,
                                asString(expectedCategories));
                Mockito.when(genreGateway.findById(any())).thenReturn(Optional.of(Genre.with(aGenre)));
                Mockito.when(categoryGateway.existsById(any())).thenReturn(expectedCategories);
                Mockito.when(genreGateway.update(any())).thenAnswer(AdditionalAnswers.returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertEquals(expectedId.getValue(), actualOutput.id());
                Mockito.verify(genreGateway, times(1)).findById(expectedId);
                Mockito.verify(categoryGateway, times(1)).existsById(eq(expectedCategories));
                Mockito.verify(genreGateway, times(1))
                                .update(Mockito.argThat(aUpdatedGenre -> Objects.equals(expectedName,
                                                aUpdatedGenre.getId())
                                                && Objects.equals(expectedName, aUpdatedGenre.getName())
                                                && Objects.equals(expectedIsActive, aUpdatedGenre.isActive())
                                                && Objects.equals(expectedCategories, aUpdatedGenre.getCategories())
                                                && Objects.equals(aGenre.getCreatedAt(), aUpdatedGenre.getCreatedAt())
                                                && aGenre.getUpdatedAt().isBefore(aUpdatedGenre.getUpdatedAt())
                                                && Objects.isNull(aUpdatedGenre.getDeletedAt())));
        }

        private List<String> asString(final List<CategoryID> ids) {
                return ids.stream().map(CategoryID::getValue).toList();
        }
}
