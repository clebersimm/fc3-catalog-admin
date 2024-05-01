package com.fullcycle.admin.catalog.application.genre.create;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

public class DefaultCreateGenreUseCase extends CreateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultCreateGenreUseCase(
            final CategoryGateway categoryGateway,
            final GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public CreateGenreOutput execute(final CreateGenreCommand aCommand) {
        final var categories = toCategoryID(aCommand.categories());
        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        final var aGenre = notification.validate(() -> Genre.newGenre(aCommand.name(), aCommand.isActive()));
        if (notification.hasError()) {
            throw new NotificationException("Could not create Aggregate Genre", notification);
        }
        aGenre.addCategories(categories);
        return CreateGenreOutput.from(this.genreGateway.create(aGenre));
    }

    private ValidationHandler validateCategories(List<CategoryID> ids) {
        final var notification = Notification.create();
        if (ids == null || ids.isEmpty()) {
            return notification;
        }
        final var retrivedIds = categoryGateway.existsById(ids);
        if (ids.size() != retrivedIds.size()) {
            final var commandIds = new ArrayList<>(ids);
            commandIds.removeAll(retrivedIds);
            final var missingIds = commandIds.stream().map(CategoryID::getValue).collect(Collectors.joining(","));
            notification.append(new Error("Some categories could not be found: %s".formatted(missingIds)));

        }
        return notification;
    }

    private List<CategoryID> toCategoryID(final List<String> categories) {
        return categories.stream().map(CategoryID::from).collect(Collectors.toList());
    }

}
