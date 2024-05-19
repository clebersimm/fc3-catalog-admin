package com.fullcycle.admin.catalog.application.genre.create;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

public class CreateGenreUseCaseTest extends UseCaseTest {
        @InjectMocks
        private DefaultCreateGenreUseCase useCase;
        @Mock
        private CategoryGateway categoryGateway;
        @Mock
        private GenreGateway genreGateway;

        @Override
        protected List<Object> getMocks() {
                return List.of(categoryGateway, genreGateway);
        }

        @Test
        public void givenAValidcommand_whenCallsCreateGenre_shouldReturnGenreId() {
                // given
                final var expectName = "Ação";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of();
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                when(genreGateway.create(any())).thenAnswer(returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertNotNull(actualOutput.id());
                Mockito.verify(genreGateway, times(1))
                                .create(Mockito.argThat(aGenre -> Objects.equals(expectName, aGenre.getName()) &&
                                                Objects.equals(expectedIsActive, aGenre.isActive()) &&
                                                Objects.equals(expectedCategories, aGenre.getCategories()) &&
                                                Objects.nonNull(aGenre.getId()) &&
                                                Objects.nonNull(aGenre.getCreatedAt()) &&
                                                Objects.nonNull(aGenre.getUpdatedAt()) &&
                                                Objects.isNull(aGenre.getDeletedAt())));
        }

        @Test
        public void givenAValidcommandWithInactiveGenre_whenCallsCreateGenre_shouldReturnGenreId() {
                // given
                final var expectName = "Ação";
                final var expectedIsActive = false;
                final var expectedCategories = List.<CategoryID>of();
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                when(genreGateway.create(any())).thenAnswer(returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertNotNull(actualOutput.id());
                Mockito.verify(genreGateway, times(1))
                                .create(Mockito.argThat(aGenre -> Objects.equals(expectName, aGenre.getName()) &&
                                                Objects.equals(expectedIsActive, aGenre.isActive()) &&
                                                Objects.equals(expectedCategories, aGenre.getCategories()) &&
                                                Objects.nonNull(aGenre.getId()) &&
                                                Objects.nonNull(aGenre.getCreatedAt()) &&
                                                Objects.nonNull(aGenre.getUpdatedAt()) &&
                                                Objects.nonNull(aGenre.getDeletedAt())));
        }

        @Test
        public void givenAValidCommandWithCategories_whenCallsCreateGenre_shouldReturnGenreId() {
                // given
                final var expectName = "Ação";
                final var expectedIsActive = true;
                final var expectedCategories = List.of(
                                CategoryID.from("123"),
                                CategoryID.from("456"));
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                when(categoryGateway.existsById(any())).thenReturn(expectedCategories);
                when(genreGateway.create(any())).thenAnswer(returnsFirstArg());
                // when
                final var actualOutput = useCase.execute(aCommand);
                // then
                Assertions.assertNotNull(actualOutput);
                Assertions.assertNotNull(actualOutput.id());
                Mockito.verify(categoryGateway, times(1)).existsById(expectedCategories);
                Mockito.verify(genreGateway, times(1))
                                .create(Mockito.argThat(aGenre -> Objects.equals(expectName, aGenre.getName()) &&
                                                Objects.equals(expectedIsActive, aGenre.isActive()) &&
                                                Objects.equals(expectedCategories, aGenre.getCategories()) &&
                                                Objects.nonNull(aGenre.getId()) &&
                                                Objects.nonNull(aGenre.getCreatedAt()) &&
                                                Objects.nonNull(aGenre.getUpdatedAt()) &&
                                                Objects.isNull(aGenre.getDeletedAt())));
        }

        @Test
        public void givenAInvalidEmptyName_whenCallsCreateGenre_shouldReturnDomainException() {
                // given
                final var expectName = " ";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of();
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                final var expectedErrorMessage = "'name' should not be empty";
                final var expectedErrorCount = 1;
                // when
                final var actualException = Assertions.assertThrows(NotificationException.class,
                                () -> useCase.execute(aCommand));
                // then
                Assertions.assertNotNull(actualException);
                Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
                Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
                Mockito.verify(categoryGateway, times(0)).existsById(any());
                Mockito.verify(genreGateway, times(0)).create(any());
        }

        @Test
        public void givenAInvalidNullName_whenCallsCreateGenre_shouldReturnDomainException() {
                // given
                final String expectName = null;
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of();
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                final var expectedErrorMessage = "'name' should not be null";
                final var expectedErrorCount = 1;
                // when
                final var actualException = Assertions.assertThrows(NotificationException.class,
                                () -> useCase.execute(aCommand));
                // then
                Assertions.assertNotNull(actualException);
                Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
                Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
                Mockito.verify(categoryGateway, times(0)).existsById(any());
                Mockito.verify(genreGateway, times(0)).create(any());
        }

        @Test
        public void givenAValidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
                // given
                final var filmes = CategoryID.from("1");
                final var series = CategoryID.from("2");
                final var documentarios = CategoryID.from("3");
                final String expectName = "ABC";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of(
                                filmes, series, documentarios);
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                final var expectedErrorMessage = "Some categories could not be found: 2,3";
                final var expectedErrorCount = 1;
                when(categoryGateway.existsById(any())).thenReturn(List.of(filmes));
                // when
                final var actualException = Assertions.assertThrows(NotificationException.class,
                                () -> useCase.execute(aCommand));
                // then
                Assertions.assertNotNull(actualException);
                Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
                Assertions.assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());
                Mockito.verify(categoryGateway, times(1)).existsById(any());
                Mockito.verify(genreGateway, times(0)).create(any());
        }

        @Test
        public void givenAInvalidValidCommand_whenCallsCreateGenreAndSomeCategoriesDoesNotExists_shouldReturnDomainException() {
                // given
                final var filmes = CategoryID.from("1");
                final var series = CategoryID.from("2");
                final var documentarios = CategoryID.from("3");
                final String expectName = " ";
                final var expectedIsActive = true;
                final var expectedCategories = List.<CategoryID>of(
                                filmes, series, documentarios);
                final var aCommand = CreateGenreCommand.with(expectName, expectedIsActive,
                                asString(expectedCategories));
                final var expectedErrorMessage1 = "Some categories could not be found: 2,3";
                final var expectedErrorMessage2 = "'name' should not be empty";
                final var expectedErrorCount = 2;
                when(categoryGateway.existsById(any())).thenReturn(List.of(filmes));
                // when
                final var actualException = Assertions.assertThrows(NotificationException.class,
                                () -> useCase.execute(aCommand));
                // then
                Assertions.assertNotNull(actualException);
                Assertions.assertEquals(expectedErrorCount, actualException.getErrors().size());
                Assertions.assertEquals(expectedErrorMessage1, actualException.getErrors().get(0).message());
                Assertions.assertEquals(expectedErrorMessage2, actualException.getErrors().get(1).message());
                Mockito.verify(categoryGateway, times(1)).existsById(any());
                Mockito.verify(genreGateway, times(0)).create(any());
        }

        private List<String> asString(final List<CategoryID> categories) {
                return categories.stream().map(CategoryID::getValue).toList();
        }
}
