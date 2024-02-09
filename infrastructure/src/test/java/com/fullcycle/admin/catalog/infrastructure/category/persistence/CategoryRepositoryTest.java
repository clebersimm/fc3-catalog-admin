package com.fullcycle.admin.catalog.infrastructure.category.persistence;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.infrastructure.category.MySQLGatewayTest;

@MySQLGatewayTest
public class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void givenAnInvalidName_whenCallSave_shoudReturnError() {
        final var aCategory = Category.newCategory("Filmes", "Muitos files", false);
        final var anEntity = CategoryJpaEntity.from(aCategory);
        anEntity.setName(null);
        categoryRepository.save(anEntity);
        final var actualException = Assertions.assertThrows(DataIntegrityViolationException.class, ()->categoryRepository.save(anEntity));
        final var actualCause = Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());
        Assertions.assertEquals(categoryRepository, categoryRepository);
        Assertions.assertEquals("name", actualCause.getPropertyName());
    }
}
