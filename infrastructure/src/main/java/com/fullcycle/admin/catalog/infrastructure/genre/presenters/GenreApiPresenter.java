package com.fullcycle.admin.catalog.infrastructure.genre.presenters;

import com.fullcycle.admin.catalog.application.category.retrive.get.CategoryOutput;
import com.fullcycle.admin.catalog.application.category.retrive.list.CategoryListOutput;
import com.fullcycle.admin.catalog.application.genre.retrive.get.GenreOutput;
import com.fullcycle.admin.catalog.application.genre.retrive.list.GenreListOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryListResponse;
import com.fullcycle.admin.catalog.infrastructure.genre.models.GenreListResponse;
import com.fullcycle.admin.catalog.infrastructure.genre.models.GenreResponse;

import java.util.function.Function;

public interface GenreApiPresenter {
    // The function and the static method do the same things, only diferent implementations
    Function<GenreOutput, GenreResponse> present = output -> new GenreResponse(
        output.id(),
        output.name(),
            output.categories(),
            output.isActive(),
        output.createdAt(), 
        output.updatedAt(),
        output.deletedAt());
    
    static GenreResponse present(final GenreOutput output) {
        return new GenreResponse(
                output.id(),
                output.name(),
                output.categories(),
                output.isActive(),
                output.createdAt(),
                output.updatedAt(),
                output.deletedAt());
    }
    static GenreListResponse present(final GenreListOutput output) {
        return new GenreListResponse(
                output.id(),
                output.name(),
                output.isActive(),
                output.createdAt(), 
                output.deletedAt());
    }
}