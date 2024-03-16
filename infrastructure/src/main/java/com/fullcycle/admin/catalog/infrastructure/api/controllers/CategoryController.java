package com.fullcycle.admin.catalog.infrastructure.api.controllers;

import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.fullcycle.admin.catalog.application.category.create.CreateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.create.CreateCategoryUseCase;
import com.fullcycle.admin.catalog.application.category.retrive.get.GetCategoryByIdUseCase;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryCommand;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryOutput;
import com.fullcycle.admin.catalog.application.category.update.UpdateCategoryUseCase;
import com.fullcycle.admin.catalog.domain.pagination.Pagination;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;
import com.fullcycle.admin.catalog.infrastructure.api.CategoryApi;
import com.fullcycle.admin.catalog.infrastructure.category.models.CategoryApiOutput;
import com.fullcycle.admin.catalog.infrastructure.category.models.CreateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.models.UpdateCategoryApiInput;
import com.fullcycle.admin.catalog.infrastructure.category.presenters.CategoryApiPresenter;

@RestController
public class CategoryController implements CategoryApi {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final GetCategoryByIdUseCase getCategoryByIdUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;

    public CategoryController(final CreateCategoryUseCase createCategoryUseCase,
            final GetCategoryByIdUseCase getCategoryByIdUseCase,
            final UpdateCategoryUseCase updateCategoryUseCase) {
        this.createCategoryUseCase = Objects.requireNonNull(createCategoryUseCase);
        this.getCategoryByIdUseCase = Objects.requireNonNull(getCategoryByIdUseCase);
        this.updateCategoryUseCase = Objects.requireNonNull(updateCategoryUseCase);
    }

    @Override
    public ResponseEntity<?> createCategory(final CreateCategoryApiInput input) {
        final var aCommand = CreateCategoryCommand.with(input.name(),
                input.description(),
                input.active() != null ? input.active() : true);
        final Function<Notification, ResponseEntity<?>> onError = ResponseEntity.unprocessableEntity()::body;
        final Function<CreateCategoryOutput, ResponseEntity<?>> onSuccess = output -> ResponseEntity
                .created(URI.create("/categories/" + output.id())).body(output);
        return this.createCategoryUseCase.execute(aCommand).fold(onError, onSuccess);
    }

    @Override
    public Pagination<?> listCategories(String search, int page, int perPage, String sort, String direction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'listCategories'");
    }

    @Override
    public CategoryApiOutput getById(String id) {
        final var categoryOutput = this.getCategoryByIdUseCase.execute(id);
        // final var categoryApiOutput =
        // CategoryApiPresenter.present.apply(categoryOutput);
        // CategoryApiPresenter.present.compose(this.getCategoryByIdUseCase::execute).apply(id);
        final var categoryApiOutput = CategoryApiPresenter.present(categoryOutput);
        return categoryApiOutput;
    }

    @Override
    public ResponseEntity<?> updateById(String id, UpdateCategoryApiInput input) {
        final var aCommand = UpdateCategoryCommand.with(id,
                input.name(),
                input.description(),
                input.active() != null ? input.active() : true);
        final Function<Notification, ResponseEntity<?>> onError = notification -> ResponseEntity.unprocessableEntity()
                .body(notification);
        final Function<UpdateCategoryOutput, ResponseEntity<?>> onSuccess = ResponseEntity::ok;
        return this.updateCategoryUseCase.execute(aCommand).fold(onError, onSuccess);
    }
}