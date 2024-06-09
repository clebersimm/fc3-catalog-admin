package com.fullcycle.admin.catalog.infrastructure.genre;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.category.CategoryMySQLGateway;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;

@MySQLGatewayTest
public class GenreMySQLGatewayTest {
    @Autowired
    private CategoryMySQLGateway categoryMysqlGateway;
    @Autowired
    private GenreMySQLGateway genreMySQLGateway;
    @Autowired
    private GenreRepository genreRepository;

    @Test
    public void testDependenciesInjected() {
        Assertions.assertNotNull(genreMySQLGateway);
        Assertions.assertNotNull(categoryMysqlGateway);
        Assertions.assertNotNull(genreRepository);
    }

    @Test
    public void givenAValidGenre_whenCallsCreateGenre_shouldPersistGenre() {
        final var filmes = categoryMysqlGateway.create(Category.newCategory("Filmes","", true));
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId());
        final var aGenre = Genre.newGenre(expectedName,expectedIsActive);
        aGenre.addCategories(expectedCategories);
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        final var actualGenre = genreMySQLGateway.create(aGenre);
        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(aGenre.getDeletedAt());
        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_whenCallsCreateGenre_shouldPersistGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var aGenre = Genre.newGenre(expectedName,expectedIsActive);
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        final var actualGenre = genreMySQLGateway.create(aGenre);
        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(aGenre.getDeletedAt());
        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), persistedGenre.getUpdatedAt());
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithoutCategories_whenCallsUpdateGenreWithCategories_shouldPersistGenre() {
        //given
        final var filmes = categoryMysqlGateway.create(Category.newCategory("Filmes","", true));
        final var series = categoryMysqlGateway.create(Category.newCategory("Series","", true));
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of(filmes.getId(), series.getId());
        final var aGenre = Genre.newGenre("aa",expectedIsActive);
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertEquals("aa", aGenre.getName());
        Assertions.assertEquals(0, aGenre.getCategories().size());
        //when
        final var updateGenre = Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories);
        final var actualGenre = genreMySQLGateway.update(updateGenre);
        //then
        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreWithCategories_whenCallsUpdateGenreCleaningCategories_shouldPersistGenre() {
        final var filmes = categoryMysqlGateway.create(Category.newCategory("Filmes","", true));
        final var series = categoryMysqlGateway.create(Category.newCategory("Series","", true));
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var aGenre = Genre.newGenre("aa",expectedIsActive);
        aGenre.addCategories(List.of(filmes.getId(), series.getId()));
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertEquals("aa", aGenre.getName());
        Assertions.assertEquals(2, aGenre.getCategories().size());
        final var updateGenre = Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories);
        final var actualGenre = genreMySQLGateway.update(updateGenre);
        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), actualGenre.getDeletedAt());
        Assertions.assertNull(aGenre.getDeletedAt());
        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertEquals(aGenre.getDeletedAt(), persistedGenre.getDeletedAt());
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreInactive_whenCallsUpdateGenreActivating_shouldPersistGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.<CategoryID>of();
        final var aGenre = Genre.newGenre(expectedName,false);
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertFalse(aGenre.isActive());
        Assertions.assertNotNull(aGenre.getDeletedAt());
        final var updateGenre = Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories);
        final var actualGenre = genreMySQLGateway.update(updateGenre);

        Assertions.assertEquals(1, genreRepository.count());

        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();
        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAValidGenreActive_whenCallsUpdateGenreInactivating_shouldPersistGenre() {
        final var expectedName = "Ação";
        final var expectedIsActive = false;
        final var expectedCategories = List.<CategoryID>of();
        final var aGenre = Genre.newGenre(expectedName,true);
        final var expectedId = aGenre.getId();
        Assertions.assertEquals(0, genreRepository.count());
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));

        Assertions.assertTrue(aGenre.isActive());
        Assertions.assertNull(aGenre.getDeletedAt());

        final var updateGenre = Genre.with(aGenre).update(expectedName, expectedIsActive, expectedCategories);
        final var actualGenre = genreMySQLGateway.update(updateGenre);

        Assertions.assertEquals(1, genreRepository.count());
        Assertions.assertEquals(expectedId, actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(expectedCategories, actualGenre.getCategories());
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(actualGenre.getDeletedAt());

        final var persistedGenre = genreRepository.findById(expectedId.getValue()).get();

        Assertions.assertEquals(expectedName, persistedGenre.getName());
        Assertions.assertEquals(expectedIsActive, persistedGenre.isActive());
        Assertions.assertEquals(expectedCategories, persistedGenre.getCategoryIDS());
        Assertions.assertEquals(aGenre.getCreatedAt(), persistedGenre.getCreatedAt());
        Assertions.assertTrue(aGenre.getUpdatedAt().isBefore(actualGenre.getUpdatedAt()));
        Assertions.assertNotNull(persistedGenre.getDeletedAt());
    }

    @Test
    public void givenAPrePersistedGenre_whenCallsDeleteById_shouldDelteGenre(){
        //given
        final var aGenre = Genre.newGenre("Ação",true);
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertEquals(1, genreRepository.count());
        //when
        genreMySQLGateway.deleteById(aGenre.getId());
        //then
        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAnInvalidGenre_whenCallsDeleteById_shouldReturnOk() {
        //given
        Assertions.assertEquals(0, genreRepository.count());
        //when
        genreMySQLGateway.deleteById(GenreID.from("123"));
        //then
        Assertions.assertEquals(0, genreRepository.count());
    }

    @Test
    public void givenAPrePersistedGenre_whenCallsFindById_shouldReturnGenre() {
        //given
        final var filmes = categoryMysqlGateway.create(Category.newCategory("Filmes","", true));
        final var series = categoryMysqlGateway.create(Category.newCategory("Series","", true));
        final var expectedName = "Ação";
        final var expectedIsActive = true;
        final var expectedCategories = List.of(filmes.getId(), series.getId());
        final var aGenre = Genre.newGenre(expectedName,expectedIsActive);
        aGenre.addCategories(expectedCategories);
        genreRepository.saveAndFlush(GenreJpaEntity.from(aGenre));
        Assertions.assertEquals(1, genreRepository.count());
        //when
        final var actualGenre = genreMySQLGateway.findById(aGenre.getId()).get();
        //then
        Assertions.assertEquals(aGenre.getId(), actualGenre.getId());
        Assertions.assertEquals(expectedName, actualGenre.getName());
        Assertions.assertEquals(expectedIsActive, actualGenre.isActive());
        Assertions.assertEquals(sorted(expectedCategories), sorted(actualGenre.getCategories()));
        Assertions.assertEquals(aGenre.getCreatedAt(), actualGenre.getCreatedAt());
        Assertions.assertEquals(aGenre.getUpdatedAt(), actualGenre.getUpdatedAt());
        Assertions.assertNull(actualGenre.getDeletedAt());
    }
    @Test
    public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
        //given
        final var expectedId = GenreID.from("123");
        Assertions.assertEquals(0, genreRepository.count());
        //when
        final var actualGenre = genreMySQLGateway.findById(expectedId);
        //then
        Assertions.assertTrue(actualGenre.isEmpty());
    }

    @Test
    public void givenEmptyGenres_whenCallFindAll_shouldReturnEmptyList() {
        //given
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = genreMySQLGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "aç,0,10,1,1,Ação",
            "dr,0,10,1,1,Drama",
            "com,0,10,1,1,Comédia",
            "cien,0,10,1,1,Ficção científica",
            "terr,0,10,1,1,Terror",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        //given
        mockGenres();
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = genreMySQLGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedGenreName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Ação",
            "name,desc,0,10,5,5,Terror",
            "createdAt,asc,0,10,5,5,Comédia",
            "createdAt,desc,0,10,5,5,Ficção científica",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnOrdered(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenreName
    ) {
        //given
        mockGenres();
        final var expectedTerms = "";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = genreMySQLGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        Assertions.assertEquals(expectedGenreName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Ação;Comédia",
            "1,2,2,5,Drama;Ficção científica",
            "2,2,1,5,Terror",
    })
    public void givenAValidTerm_whenCallFindAll_shouldReturnFiltered(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedGenres
    ) {
        //given
        mockGenres();
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = genreMySQLGateway.findAll(aQuery);

        Assertions.assertEquals(expectedPage, actualPage.currentPage());
        Assertions.assertEquals(expectedPerPage, actualPage.perPage());
        Assertions.assertEquals(expectedTotal, actualPage.total());
        Assertions.assertEquals(expectedItemsCount, actualPage.items().size());
        int index = 0;
        for(final var expectedName: expectedGenres.split(";")){
            final var actualName = actualPage.items().get(index).getName();
            Assertions.assertEquals(expectedName, actualName);
            index++;
        }
    }

    private void mockGenres() {
        genreRepository.saveAllAndFlush(List.of(
                GenreJpaEntity.from(Genre.newGenre("Comédia", true)),
                GenreJpaEntity.from(Genre.newGenre("Ação", true)),
                GenreJpaEntity.from(Genre.newGenre("Drama", true)),
                GenreJpaEntity.from(Genre.newGenre("Terror", true)),
                GenreJpaEntity.from(Genre.newGenre("Ficção científica", true))
        ));
    }

    private List<CategoryID> sorted(final List<CategoryID> expectedCategories) {
        return expectedCategories.stream()
                .sorted(Comparator.comparing(CategoryID::getValue))
                .toList();
    }
}
