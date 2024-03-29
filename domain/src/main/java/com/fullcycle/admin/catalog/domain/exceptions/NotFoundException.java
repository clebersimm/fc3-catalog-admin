package com.fullcycle.admin.catalog.domain.exceptions;

import java.util.Collections;
import java.util.List;

import com.fullcycle.admin.catalog.domain.AggregateRoot;
import com.fullcycle.admin.catalog.domain.Identifier;
import com.fullcycle.admin.catalog.domain.validation.Error;

public class NotFoundException extends DomainException {

    protected NotFoundException(String aMessage, List<Error> anErrors) {
        super(aMessage, anErrors);
    }

    public static NotFoundException with(
            final Class<? extends AggregateRoot<?>> anAggregate,
            final Identifier id) {
        final var anError = "%s with ID %s was not found".formatted(anAggregate.getSimpleName(), id.getValue());
        return new NotFoundException(anError, Collections.emptyList());
    }
}
