package com.fullcycle.admin.catalog.infrastructure.category;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fullcycle.admin.catalog.domain.category.Category;
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
    
}
