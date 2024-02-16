package com.fullcycle.admin.catalog.infrastructure.category;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fullcycle.admin.catalog.MySQLGatewayTest;
import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;

@MySQLGatewayTest
public class CategoryMysqlSQLGatewayTest {
    @Autowired
    private CategoryMysqlGateway categoryMysqlGateway;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory(){
        final var expectedName = "Filmes";
        final var expectedDescription = "Uma categoria de filme";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        final var actualCategory = categoryMysqlGateway.create(aCategory);
        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();
        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(aCategory.getId().getValue(), actualEntity.getId());
    }

    @Test
    public void givenAValidCategory_whenCallsUpdate_shouldReturnACategoryUpdated(){
        final var expectedName = "Filmes";
        final var expectedDescription = "Uma categoria de filme";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory("filme 2", "description", expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));
        
        Assertions.assertEquals(1, categoryRepository.count());
        final var aUpdatedCategory = aCategory.clone().update(expectedName, expectedDescription, expectedIsActive);
        final var actualCategory = categoryMysqlGateway.update(aUpdatedCategory);
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());
        
        final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();
        Assertions.assertEquals(1, categoryRepository.count());
        Assertions.assertEquals(expectedName, actualEntity.getName());
        Assertions.assertEquals(expectedDescription, actualEntity.getDescription());
        Assertions.assertEquals(expectedIsActive, actualEntity.isActive());
        Assertions.assertEquals(actualCategory.getCreatedAt(), actualEntity.getCreatedAt());
        Assertions.assertEquals(actualCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
        Assertions.assertEquals(actualCategory.getDeletedAt(), actualEntity.getDeletedAt());
        Assertions.assertNull(actualEntity.getDeletedAt());
        Assertions.assertEquals(actualCategory.getId().getValue(), actualEntity.getId());
    }
    
    @Test
    public void givenAPrePersitedCategoryAndValidId_whenTryToDeleteIt_shouldDeleteCategory(){ 
        final var aCategory = Category.newCategory("filme", "filme legal", true);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));
        Assertions.assertEquals(1, categoryRepository.count());
        categoryMysqlGateway.deleteById(aCategory.getId());
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenInAndValidId_whenTryToDeleteIt_shouldDeleteCategory(){ 
        Assertions.assertEquals(0, categoryRepository.count());
        categoryMysqlGateway.deleteById(CategoryID.from("invalid"));
        Assertions.assertEquals(0, categoryRepository.count());
    }

    @Test
    public void givenAPrePersitedCategoryAndValidCategoryId_whenCallsFindById_shouldReturnACategory(){
        final var expectedName = "Filmes";
        final var expectedDescription = "Uma categoria de filme";
        final var expectedIsActive = true;
        final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));
        
        Assertions.assertEquals(1, categoryRepository.count());
        final var actualCategory = categoryMysqlGateway.findById(aCategory.getId()).get();
        Assertions.assertEquals(1, categoryRepository.count());

        Assertions.assertEquals(expectedName, actualCategory.getName());
        Assertions.assertEquals(expectedDescription, actualCategory.getDescription());
        Assertions.assertEquals(expectedIsActive, actualCategory.isActive());
        Assertions.assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
        Assertions.assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
        Assertions.assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
        Assertions.assertNull(actualCategory.getDeletedAt());
        Assertions.assertEquals(aCategory.getId(), actualCategory.getId());        
    }

    @Test
    public void givenValidCategoryIdNotStored_whenCallsFindById_shouldReturnEmpty() {
        Assertions.assertEquals(0, categoryRepository.count());
        final var actualCategory = categoryMysqlGateway.findById(CategoryID.from("none"));
        Assertions.assertTrue(actualCategory.isEmpty());
    }

    @Test
    public void givenPrePersistedCategories_whenCallFindAll_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;
        final var filmes = Category.newCategory("Filmes", "", true);
        final var series = Category.newCategory("Series", "", true);
        final var docs = Category.newCategory("Docs", "", true);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(docs)
        ));
        Assertions.assertEquals(3, categoryRepository.count());
        final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(docs.getId(), actualResult.items().get(0).getId());
    }
    
    @Test
    public void givenEmptyCategories_whenCallFindAll_shouldReturnEmptyPage() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 0;
        Assertions.assertEquals(0, categoryRepository.count());
        final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        final var actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
    }
    
    @Test
    public void givenFollowPagination_whenCallFindAllWithPage1_shouldReturnPaginated() {
        var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 3;
        final var filmes = Category.newCategory("Filmes", "", true);
        final var series = Category.newCategory("Series", "", true);
        final var docs = Category.newCategory("Docs", "", true);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(docs)
        ));
        Assertions.assertEquals(3, categoryRepository.count());
        var query = new CategorySearchQuery(0, 1, "", "name", "asc");
        var actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(docs.getId(), actualResult.items().get(0).getId());
        //Page1
        expectedPage = 1;
        query = new CategorySearchQuery(1, 1, "", "name", "asc");
        actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());
        //Page2
        expectedPage = 2;
        query = new CategorySearchQuery(2, 1, "", "name", "asc");
        actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(series.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndDocAsTerm_whenCallFindAllAdnTermsMatchesCategoryName_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;
        final var filmes = Category.newCategory("Filmes", "", true);
        final var series = Category.newCategory("Series", "", true);
        final var docs = Category.newCategory("Docs", "", true);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(docs)
        ));
        Assertions.assertEquals(3, categoryRepository.count());
        final var query = new CategorySearchQuery(0, 1, "doc", "name", "asc");
        final var actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(docs.getId(), actualResult.items().get(0).getId());
    }

    @Test
    public void givenPrePersistedCategoriesAndAsMaisAssistidasAsTerm_whenCallFindAllAdnTermsMatchesCategoryDescription_shouldReturnPaginated() {
        final var expectedPage = 0;
        final var expectedPerPage = 1;
        final var expectedTotal = 1;
        final var filmes = Category.newCategory("Filmes", "A categoria mais assistidas", true);
        final var series = Category.newCategory("Series", "Uma categoria assistida", true);
        final var docs = Category.newCategory("Docs", "A categoria menos assistida", true);
        Assertions.assertEquals(0, categoryRepository.count());
        categoryRepository.saveAll(List.of(
            CategoryJpaEntity.from(filmes),
            CategoryJpaEntity.from(series),
            CategoryJpaEntity.from(docs)
        ));
        Assertions.assertEquals(3, categoryRepository.count());
        final var query = new CategorySearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
        final var actualResult = categoryMysqlGateway.findAll(query);
        Assertions.assertEquals(expectedPage, actualResult.currentPage());
        Assertions.assertEquals(expectedPerPage, actualResult.perPage());
        Assertions.assertEquals(expectedTotal, actualResult.total());
        Assertions.assertEquals(expectedPerPage, actualResult.items().size());
        Assertions.assertEquals(filmes.getId(), actualResult.items().get(0).getId());
    }
}
