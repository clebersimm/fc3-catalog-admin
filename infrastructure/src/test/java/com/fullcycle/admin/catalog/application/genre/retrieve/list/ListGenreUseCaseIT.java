package com.fullcycle.admin.catalog.application.genre.retrieve.list;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.application.genre.retrive.list.GenreListOutput;
import com.fullcycle.admin.catalog.application.genre.retrive.list.ListGenreUseCase;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@IntegrationTest
public class ListGenreUseCaseIT {
    @Autowired
    private ListGenreUseCase useCase;
    @Autowired
    private GenreGateway genreGateway;
    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void givenAValidQuery_whenCallsListGenre_shouldReturnGenres() {
        // given
        final var genres = List.of(
                Genre.newGenre("Ação", true),
                Genre.newGenre("Suspense", true),
                Genre.newGenre("Aventura", true));
        genreRepository.saveAllAndFlush(genres.stream().map(GenreJpaEntity::from).toList());

        final var expectedPage = 0;
        final var exptectedPerPage = 10;
        final var exptectedTerms = "A";
        final var expectedSort = "createdAt";
        final var expectedDirection = "asc";
        final var expectedTotal = 3;
        final var expectedItems = genres.stream().map(GenreListOutput::from).toList();
        final var aQuery = new SearchQuery(expectedPage, exptectedPerPage, exptectedTerms, expectedSort,
                expectedDirection);
        // when
        final var actualOutput = useCase.execute(aQuery);
        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(exptectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertTrue(expectedItems.size() == actualOutput.items().size() &&
                expectedItems.containsAll(actualOutput.items())
                );
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
        final var aQuery = new SearchQuery(expectedPage, exptectedPerPage, exptectedTerms, expectedSort,
                expectedDirection);
        // when
        final var actualOutput = useCase.execute(aQuery);
        // then
        Assertions.assertEquals(expectedPage, actualOutput.currentPage());
        Assertions.assertEquals(exptectedPerPage, actualOutput.perPage());
        Assertions.assertEquals(expectedTotal, actualOutput.total());
        Assertions.assertEquals(expectedItems, actualOutput.items());
    }
}
