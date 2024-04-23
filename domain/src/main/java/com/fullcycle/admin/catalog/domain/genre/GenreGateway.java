package com.fullcycle.admin.catalog.domain.genre;

import java.util.Optional;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.pagination.SearchQuery;

public interface GenreGateway {
    Genre create(Genre aGenre);
    void deleteById(GenreID anId);
    Optional<Genre> findById(GenreID anID);
    Genre update(Genre genre);
    Pagination<Genre> findAll(SearchQuery aQuery);
}
