package com.fullcycle.admin.catalog.domain.genre;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.exceptions.NotificationException;
import com.fullcycle.admin.catalog.domain.utils.InstantUtils;
import com.fullcycle.admin.catalog.domain.validation.ValidationHandler;
import com.fullcycle.admin.catalog.domain.validation.handler.Notification;

public class Genre extends AggregateRoot<GenreID> {

    private String name;
    private boolean active;
    private List<CategoryID> categories;
    private Instant createdAt;
    private Instant deletedAt;
    private Instant updatedAt;

    protected Genre(final GenreID anID,
            final String name,
            final boolean isActive,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt) {
        super(anID);
        this.name = name;
        this.active = isActive;
        this.categories = categories;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        validate();
    }

    private void validate() {
        final var notification = Notification.create();
        validate(notification);
        if (notification.hasError()) {
            throw new NotificationException("Failed to create a Aggregate Genre", notification);
        }
    }

    public static Genre newGenre(final String name, final boolean active) {
        final var id = GenreID.unique();
        final var now = InstantUtils.now();
        final var deletedAt = active ? null : now;
        return new Genre(id, name, active, new ArrayList<>(), now, now, deletedAt);
    }

    public static Genre with(final GenreID anID,
            final String name,
            final boolean isActive,
            final List<CategoryID> categories,
            final Instant createdAt,
            final Instant updatedAt,
            final Instant deletedAt) {
        return new Genre(anID, name, isActive, categories, createdAt, updatedAt, deletedAt);
    }

    public static Genre with(final Genre aGenre) {
        return new Genre(aGenre.id, aGenre.name, aGenre.active, new ArrayList<>(aGenre.categories), aGenre.createdAt,
                aGenre.updatedAt,
                aGenre.deletedAt);
    }

    @Override
    public void validate(ValidationHandler handler) {
        new GenreValidator(this, handler).validate();
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<CategoryID> getCategories() {
        return Collections.unmodifiableList(categories);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Genre deactivate() {
        if(getDeletedAt() == null){
            this.deletedAt = InstantUtils.now();
        }
        this.active = false;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre activate() {
        this.deletedAt = null;
        this.active = true;
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre update(final String aName,final boolean isActive,final  List<CategoryID> categories) {
        this.name = aName;
        if(isActive){
            activate();
        } else {
            deactivate();
        }
        this.categories = new ArrayList<>(categories != null ? categories : Collections.emptyList());
        this.updatedAt = InstantUtils.now();
        validate();
        return this;
    }

    public Genre addCategory(final CategoryID aCategoryID) {
        if(aCategoryID == null){
            return this;
        }
        this.categories.add(aCategoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }

    public Genre removeCategory(CategoryID aCategoryID) {
        if(aCategoryID == null){
            return this;
        }
        this.categories.remove(aCategoryID);
        this.updatedAt = InstantUtils.now();
        return this;
    }
}
