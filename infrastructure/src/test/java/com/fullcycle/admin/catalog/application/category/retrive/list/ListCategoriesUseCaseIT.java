package com.fullcycle.admin.catalog.application.category.retrive.list;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import com.fullcycle.admin.catalog.IntegrationTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;

@IntegrationTest
public class ListCategoriesUseCaseIT {
    @Autowired
    private ListCategoriesUseCase useCase;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockUp(){
        final var categories = List.of(
            Category.newCategory("Filmes", "sem descrição", true),
            Category.newCategory("Netflix Originals", "netflix", true),
            Category.newCategory("Amazon Originals", "aws", true),
            Category.newCategory("Documentários", "docs", true),
            Category.newCategory("Sports", "sports", true),
            Category.newCategory("Kids", "kids", true),
            Category.newCategory("Series", "", true)
        ).stream().map(CategoryJpaEntity::from).toList();
        categoryRepository.saveAllAndFlush(categories);
    }

    @Test
    public void givenAValidTerm_whenTermDoesntMatchsPrePersisted_shoudReturnEmptyPage(){
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "HBO";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedItemsCount = 0;
        final var expectedTotal = 0;
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
    }

    @ParameterizedTest
    @CsvSource({
        "fil,0,10,1,1,Filmes",
        "net,0,10,1,1,Netflix Originals",
        "ZON,0,10,1,1,Amazon Originals",
        "KI,0,10,1,1,Kids"
    })
    public void givenAValidTerm_whenCallListCategories_shouldReturnCategoriesFiltered(
        final String expectedTerms,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }


    @ParameterizedTest
    @CsvSource({
        "name,asc,0,10,7,7,Amazon Originals",
        "name,desc,0,10,7,7,Sports",
        "createdAt,desc,0,10,7,7,Series",
    })
    public void givenAValidSortAndDirection_whenCallsListCategories_thenShouldCategoriesOrdered(
        final String expectedSort,
        final String expectedDirection,
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoryName
    ) {
        final var expectedTerms = "";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedCategoryName, actualResult.items().get(0).name());
    }

    @ParameterizedTest
    @CsvSource({
        "0,2,2,7,Amazon Originals;Documentários",
        "1,2,2,7,Filmes;Kids",
        "2,2,2,7,Netflix Originals;Series",
        "3,2,1,7,Sports",
    })
    public void givenAValidPage_whenCallsListCategories_shouldReturnCategoriesPaginated(
        final int expectedPage,
        final int expectedPerPage,
        final int expectedItemsCount,
        final long expectedTotal,
        final String expectedCategoriesName)
    {
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var aQuery = new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);
        final var actualResult = useCase.execute(aQuery);
        Assertions.assertEquals(expectedItemsCount, actualResult.items().size());
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        int index = 0;
        for(final String expectedName: expectedCategoriesName.split(";")){
            Assertions.assertEquals(expectedName, actualResult.items().get(index).name());
            index++;
        }
    }
}
