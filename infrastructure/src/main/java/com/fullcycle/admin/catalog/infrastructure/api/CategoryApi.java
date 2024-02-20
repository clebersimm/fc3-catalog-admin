package com.fullcycle.admin.catalog.infrastructure.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;

@RequestMapping(value="categories")
public interface CategoryApi {
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createCategory();
    @GetMapping
    Pagination<?> listCategories(
        @RequestParam(name="search", required = false, defaultValue = "") final String search,
        @RequestParam(name="page", required = false, defaultValue = "0") final int page,
        @RequestParam(name="perPage", required = false, defaultValue = "name") final int perPage,
        @RequestParam(name="sort", required = false, defaultValue = "10") final String sort,
        @RequestParam(name="dir", required = false, defaultValue = "asc") final String direction
    );
}
