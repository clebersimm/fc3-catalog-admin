package com.fullcycle.admin.catalog.application.genre.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.fullcycle.admin.catalog.domain.category.CategoryGateway;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.DomainException;
import com.fullcycle.admin.catalog.domain.exceptions.NotFoundException;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.genre.Genre;
import com.fullcycle.admin.catalog.domain.genre.GenreGateway;
import com.fullcycle.admin.catalog.domain.genre.GenreID;
import com.fullcycle.admin.catalog.domain.validation.Error;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

public class DefaultUpdateGenreUseCase extends UpdateGenreUseCase {

    private final CategoryGateway categoryGateway;
    private final GenreGateway genreGateway;

    public DefaultUpdateGenreUseCase(CategoryGateway categoryGateway, GenreGateway genreGateway) {
        this.categoryGateway = Objects.requireNonNull(categoryGateway);
        this.genreGateway = Objects.requireNonNull(genreGateway);
    }

    @Override
    public UpdateGenreOutput execute(UpdateGenreCommand aCommand) {
        final var anId = GenreID.from(aCommand.id());
        final var categories = toCategoryId(aCommand.categories());
        final var aGenre = this.genreGateway.findById(anId).orElseThrow(notFound(anId));
        final var notification = Notification.create();
        notification.append(validateCategories(categories));
        notification.validate(() -> aGenre.update(aCommand.name(), aCommand.isActive(), categories));
        if (notification.hasError()) {
            throw new NotificationException("Could not update Aggregate Genre %s".formatted(aCommand.id()),
                    notification);
        }
        return UpdateGenreOutput.from(this.genreGateway.update(aGenre));
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

    private Supplier<DomainException> notFound(final GenreID anId) {
        return () -> NotFoundException.with(Genre.class, anId);
    }

    private List<CategoryID> toCategoryId(final List<String> categories) {
        return categories.stream().map(CategoryID::from).toList();
    }

}
