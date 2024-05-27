package com.fullcycle.admin.catalog.application.genre.retrive.list;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.fullcycle.admin.catalog.application.UseCaseTest;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

public class ListGenreUseCaseTest extends UseCaseTest {
    @InjectMocks
    private DefaultListGenreUseCase useCase;
    @Mock
    private GenreGateway genreGateway;

    @Override
    protected List<Object> getMocks() {
        return List.of(genreGateway);
    }

    @Test
    public void givenAValidQuery_whenCallsListGenre_shouldReturnGenres() {
        // given
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Suspense", true),
                Genre.newGenre("Aventura", true));
        final var expectedPage = 0;
        final var exptectedPerPage = 10;
        final var exptectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 3;
        final var expectedItems = genres.stream().map(GenreListOutput::from).toList();
        final var expectedPagination = new Pagination<>(expectedPage, exptectedPerPage, expectedTotal, genres);
        Mockito.when(genreGateway.findAll(any())).thenReturn(expectedPagination);
        final var aQuery = new SearchQuery(expectedPage, exptectedPerPage, exptectedTerms, expectedSort,
                expectedDirection);
        // when
        final var actualOutput = useCase.execute(aQuery);
        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(exptectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());
        Mockito.verify(genreGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListGenreAndResultIsEmpty_shouldReturnGenres() {
        // given
        final var genres = List.<Genre>of();
        final var expectedPage = 0;
        final var exptectedPerPage = 10;
        final var exptectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;
        final var expectedItems = List.<GenreListOutput>of();
        final var expectedPagination = new Pagination<>(expectedPage, exptectedPerPage, expectedTotal, genres);
        Mockito.when(genreGateway.findAll(any())).thenReturn(expectedPagination);
        final var aQuery = new SearchQuery(expectedPage, exptectedPerPage, exptectedTerms, expectedSort,
                expectedDirection);
        // when
        final var actualOutput = useCase.execute(aQuery);
        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(exptectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());
        Mockito.verify(genreGateway, times(1)).findAll(eq(aQuery));
    }

    @Test
    public void givenAValidQuery_whenCallsListGenreThrowsError_shouldReceiveException() {
        // given
        final var expectedPage = 0;
        final var exptectedPerPage = 10;
        final var exptectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var errorMessage = "Gateway error";
        Mockito.when(genreGateway.findAll(any())).thenThrow(new IllegalStateException(errorMessage));
        final var aQuery = new SearchQuery(expectedPage, exptectedPerPage, exptectedTerms, expectedSort,
                expectedDirection);
        // when
        final var actualOutput = Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));
        // then
        Assertions.assertEquals(errorMessage, actualOutput.getMessage());
        Mockito.verify(genreGateway, times(1)).findAll(eq(aQuery));
    }

}
