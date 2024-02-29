package com.fullcycle.admin.catalog.infrastructure.category.presenters;

import java.util.function.Function;

import com.fullcycle.admin.catalog.application.category.retrive.get.CategoryOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;

public interface CategoryApiPresenter {
    // The function and the static method do the same things, only diferent implementations
    Function<CategoryOutput, CategoryApiOutput> present = output -> new CategoryApiOutput(
        output.id().getValue(),
        output.name(),
        output.description(), 
        output.isActive(), 
        output.createdAt(), 
        output.updateddAt(), 
        output.deleteddAt());
    
    static CategoryApiOutput present(final CategoryOutput output) {
        return new CategoryApiOutput(
                output.id().getValue(),
                output.name(),
                output.description(), 
                output.isActive(), 
                output.createdAt(), 
                output.updateddAt(), 
                output.deleteddAt());
    }
}