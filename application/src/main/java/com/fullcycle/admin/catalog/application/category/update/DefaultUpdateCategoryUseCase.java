package com.fullcycle.admin.catalog.application.category.update;

import java.util.Objects;
import java.util.function.Supplier;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
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
                .orElseThrow(notFound(andId));
        final var notifications = Notification.create();
        aCategory.update(aCommand.name(), aCommand.description(), aCommand.isActive()).validate(notifications);
        return notifications.hasError() ? API.Left(notifications) : update(aCategory);
    }

    private Either<Notification, UpdateCategoryOutput> update(final Category aCategory) {
        return API.Try(() -> this.categoryGateway.update(aCategory)).toEither().bimap(Notification::create,
                UpdateCategoryOutput::from);
    }

    private Supplier<NotFoundException> notFound(final CategoryID anId) {
        return ()-> NotFoundException.with(Category.class, anId);
    }

}
