package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.infrastructure.api.CategoryApi;

@RestController
public class CategoryController implements CategoryApi {

    @Override
    public ResponseEntity<?> createCategory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCategory'");
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listCategories'");
    }
    
}
