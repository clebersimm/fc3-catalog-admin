package com.fullcycle.admin.catalog.application.genre.retrive.get;

import java.time.Instant;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;

public record GenreOutput(String id, String name, boolean isActive, java.util.List<String> categories,
        Instant createdAt, Instant updatedAt, Instant deletedAt) {
    public static GenreOutput from(final Genre genre) {
        return new GenreOutput(genre.getId().getValue(), genre.getName(), genre.isActive(),
                genre.getCategories().stream().map(CategoryID::getValue).toList(), genre.getCreatedAt(),
                genre.getUpdatedAt(), genre.getDeletedAt());
    }
}
