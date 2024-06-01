package com.fullcycle.admin.catalog;
import java.util.Collection;
import java.util.List;

import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infrastructure.genre.persistence.GenreRepository;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class CleanUpExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        final var aContext = SpringExtension.getApplicationContext(context);

        final var repositories = SpringExtension.getApplicationContext(context).getBeansOfType(CrudRepository.class).values();
        cleanUp(List.of(
                aContext.getBean(GenreRepository.class),
                aContext.getBean(CategoryRepository.class)
        ));
        final var em = aContext.getBean(TestEntityManager.class);
        em.flush();
        em.clear();
    }

    private void cleanUp(final Collection<CrudRepository> repositories){
        repositories.forEach(CrudRepository::deleteAll);
    }
}
