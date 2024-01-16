package com.fullcycle.admin.catalog.application.category.retrive.get;

import java.time.Instant;

import com.fullcycle.admin.catalog.domain.category.Category;
import com.fullcycle.admin.catalog.domain.category.CategoryID;

public record CategoryOutput(
        CategoryID id,
        String name,
        String description,
        boolean isActive,
        Instant createdAt,
        Instant updateddAt,
        Instant deleteddAt) {
    public static CategoryOutput from(final Category aCategory) {
        return new CategoryOutput(aCategory.getId(), 
            aCategory.getName(), 
            aCategory.getDescription(),
            aCategory.isActive(), 
            aCategory.getCreatedAt(), 
            aCategory.getUpdatedAt(), 
            aCategory.getDeletedAt());
    }
}
