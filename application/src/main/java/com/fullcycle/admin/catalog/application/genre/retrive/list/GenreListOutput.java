package com.fullcycle.admin.catalog.application.genre.retrive.list;

import java.time.Instant;

import com.fullcycle.admin.catalog.domain.category.CategoryID;
import com.fullcycle.admin.catalog.domain.genre.Genre;

public record GenreListOutput(String id, String name, boolean isActive, java.util.List<String> categories, Instant createdAt,
        Instant deletedAt) {
    public static GenreListOutput from(
            final Genre genre) {
        return new GenreListOutput(genre.getId().getValue(), genre.getName(), genre.isActive(),
                genre.getCategories().stream().map(CategoryID::getValue).toList(), genre.getCreatedAt(),
                genre.getDeletedAt());
    }
}
