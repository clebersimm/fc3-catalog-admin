package com.fullcycle.admin.catalog.infrastructure.genre;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.infrastructure.category.CategoryMysqlGateway;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreCategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@MySQLGatewayTest
public class GatewayMySQLGatewayTest {
    @Autowired
    private CategoryMysqlGateway categoryMysqlGateway;
    @Autowired
    private GenreMySQLGateway genreMySQLGateway;
    @Autowired
    private GenreRepository genreRepository;

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
}
