package com.fullcycle.admin.catalog.application.category.retrive.list;

import java.util.Objects;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;

public class DefaultListCategoriesUseCase extends ListCategoriesUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultListCategoriesUseCase(CategoryGateway categoryGateway) {
        Objects.requireNonNull(categoryGateway);
        this.categoryGateway = categoryGateway;
    }



    @Override
    public Pagination<CategoryListOutput> execute(SearchQuery aQuery) {
        return this.categoryGateway.findAll(aQuery)
            .map(CategoryListOutput::from);
    }

}
