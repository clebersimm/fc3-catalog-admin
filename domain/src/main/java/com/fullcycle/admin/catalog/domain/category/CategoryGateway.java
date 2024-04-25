package com.fullcycle.admin.catalog.domain.category;

import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;

import java.util.List;
import java.util.Optional;

public interface CategoryGateway {
    Category create(Category aCategory);

    void deleteById(CategoryID anId);

    Optional<Category> findById(CategoryID anId);

    Category update(Category anCategory);

    Pagination<Category> findAll(SearchQuery aQuery);

    List<CategoryID> existsById(Iterable<CategoryID> ids);
}
