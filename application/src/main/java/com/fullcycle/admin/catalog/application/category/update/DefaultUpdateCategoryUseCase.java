package com.fullcycle.admin.catalog.application.category.update;

import java.util.Objects;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

import io.vavr.API;
import io.vavr.control.Either;

public class DefaultUpdateCategoryUseCase extends UpdateCategoryUseCase {

    private final CategoryGateway categoryGateway;

    public DefaultUpdateCategoryUseCase(final CategoryGateway categoryGateway) {
        Objects.nonNull(categoryGateway);
        this.categoryGateway = categoryGateway;
    }

    @Override
    public Either<Notification, UpdateCategoryOutput> execute(UpdateCategoryCommand aCommand) {
        final var andId = CategoryID.from(aCommand.id());
        final var aCategory = this.categoryGateway.findById(andId)
                .orElseThrow(() -> DomainException
                        .with(new Error("Category with ID %s was not found".formatted(andId.getValue()))));
        final var notifications = Notification.create();
        aCategory.update(aCommand.name(), aCommand.description(), aCommand.isActive()).validate(notifications);
        ;
        return notifications.hasError() ? API.Left(notifications) : update(aCategory);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category aCategory) {
        return API.Try(() -> this.categoryGateway.update(aCategory)).toEither().bimap(Notification::create,
                UpdateCategoryOutput::from);
    }

}
