package com.fullcycle.admin.catalog.infrastructure.configuration.usecases;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.create.DefaultCreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DefaultDeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.delete.DeleteCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.get.DefaultGetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.list.DefaultListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.list.ListCategoriesUseCase;
import com.fullcycle.admin.catalog.application.category.update.DefaultUpdateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;

@Configuration
public class CategoryUseCaseConfig {
    private final CategoryGateway categoryGateway;

    public CategoryUseCaseConfig(final CategoryGateway categoryGateway){
        this.categoryGateway = categoryGateway;
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(){
        return new DefaultCreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase() {
        return new DefaultUpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public GetCategoryByIdUseCase getCategoryByIdUseCase() {
        return new DefaultGetCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public ListCategoriesUseCase listCategoriesUseCase() {
        return new DefaultListCategoriesUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoriesUseCase() {
        return new DefaultDeleteCategoryUseCase(categoryGateway);
    }
}
