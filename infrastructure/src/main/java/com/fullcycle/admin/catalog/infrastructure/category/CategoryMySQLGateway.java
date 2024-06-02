package com.fullcycle.admin.catalog.infrastructure.category;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalog.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalog.infrastructure.utils.SpecificationUtils;

@Component
public class CategoryMySQLGateway implements CategoryGateway {
    private final CategoryRepository repository;

    public CategoryMySQLGateway(final CategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category create(final Category aCategory) {
        return save(aCategory);
    }

    @Override
    public void deleteById(final CategoryID anId) {
        if (this.repository.existsById(anId.getValue()))
            this.repository.deleteById(anId.getValue());
    }

    private Category save(final Category aCategory) {
        return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
    }

    @Override
    public Optional<Category> findById(final CategoryID anId) {
        return this.repository.findById(anId.getValue()).map(CategoryJpaEntity::toAggregate);
    }

    @Override
    public Category update(final Category aCategory) {
        return save(aCategory);
    }

    @Override
    public Pagination<Category> findAll(final SearchQuery aQuery) {
        final var page = PageRequest.of(aQuery.page(), aQuery.perPage(),
                Sort.by(Direction.fromString(aQuery.direction()), aQuery.sort()));
        final var specication = Optional.ofNullable(aQuery.terms())
                .filter(str -> !str.isBlank())
                .map(str -> {
                    return SpecificationUtils.<CategoryJpaEntity>like("name", str)
                            .or(SpecificationUtils.<CategoryJpaEntity>like("description", str));
                }).orElse(null);
        final var pageResult = this.repository.findAll(Specification.where(specication), page);
        return new Pagination<>(pageResult.getNumber(), pageResult.getSize(),
                pageResult.getTotalElements(), pageResult.map(CategoryJpaEntity::toAggregate).toList());
    }

    @Override
    public List<CategoryID> existsById(Iterable<CategoryID> ids) {
        return Collections.emptyList();
    }
}
